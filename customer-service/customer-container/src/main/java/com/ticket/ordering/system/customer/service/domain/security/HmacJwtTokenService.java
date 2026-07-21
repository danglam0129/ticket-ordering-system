package com.ticket.ordering.system.customer.service.domain.security;

import com.ticket.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.ticket.ordering.system.customer.service.domain.dto.token.JwtTokenClaims;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.JwtTokenService;
import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HmacJwtTokenService implements JwtTokenService {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final CustomerServiceConfigData customerServiceConfigData;

    public HmacJwtTokenService(CustomerServiceConfigData customerServiceConfigData) {
        this.customerServiceConfigData = customerServiceConfigData;
    }

    @Override
    public String generateAccessToken(Customer customer, Instant issuedAt, Instant expiresAt) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = "{" +
                    "\"iss\":\"" + escape(customerServiceConfigData.getJwtIssuer()) + "\"," +
                    "\"sub\":\"" + customer.getId().getValue() + "\"," +
                    "\"username\":\"" + escape(customer.getUsername()) + "\"," +
                    "\"role\":\"" + customer.getRole().name() + "\"," +
                    "\"iat\":" + issuedAt.getEpochSecond() + "," +
                    "\"exp\":" + expiresAt.getEpochSecond() +
                    "}";

            String encodedHeader = encode(header);
            String encodedPayload = encode(payload);
            String signingInput = encodedHeader + "." + encodedPayload;
            return signingInput + "." + sign(signingInput);
        } catch (Exception e) {
            throw new IllegalStateException("Could not generate JWT access token", e);
        }
    }

    @Override
    public Optional<JwtTokenClaims> parseAccessToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }
            String signingInput = parts[0] + "." + parts[1];
            if (!MessageDigest.isEqual(sign(signingInput).getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }
            String payload = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            Instant expiresAt = Instant.ofEpochSecond(getLongClaim(payload, "exp"));
            if (!expiresAt.isAfter(Instant.now())) {
                return Optional.empty();
            }
            return Optional.of(JwtTokenClaims.builder()
                    .customerId(UUID.fromString(getStringClaim(payload, "sub")))
                    .username(getStringClaim(payload, "username"))
                    .role(CustomerRole.valueOf(getStringClaim(payload, "role")))
                    .expiresAt(expiresAt)
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String encode(String value) {
        return URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String signingInput) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKey = new SecretKeySpec(
                customerServiceConfigData.getJwtSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        mac.init(secretKey);
        return URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
    }

    private String getStringClaim(String payload, String claimName) {
        Matcher matcher = Pattern.compile("\"" + claimName + "\"\\s*:\\s*\"([^\"]*)\"").matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing JWT claim: " + claimName);
        }
        return matcher.group(1);
    }

    private long getLongClaim(String payload, String claimName) {
        Matcher matcher = Pattern.compile("\"" + claimName + "\"\\s*:\\s*(\\d+)").matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing JWT claim: " + claimName);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
