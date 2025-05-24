package com.harukite.canteen.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT (JSON Web Token) 工具类。
 * 负责 JWT Token 的生成、解析和验证。
 */
@Component
public class JwtUtil
{

    // 从 application.properties 或 application.yml 中读取 JWT 密钥
    // 建议使用强密钥，例如 256 位（32 字节）或 512 位（64 字节）
    // 您可以在 application.yml 中添加: jwt.secret=YOUR_SUPER_SECRET_KEY_THAT_IS_AT_LEAST_32_CHARS_LONG
    @Value("${jwt.secret}")
    private String secret;

    // Token 的过期时间（毫秒），例如 1 小时
    @Value("${jwt.expiration}")
    private long expiration; // 例如 3600000L (1小时)

    /**
     * 从 Token 中提取用户名。
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从 Token 中提取过期日期。
     *
     * @param token JWT Token
     * @return 过期日期
     */
    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从 Token 中提取特定声明。
     *
     * @param token          JWT Token
     * @param claimsResolver 用于从 Claims 中解析声明的函数
     * @param <T>            声明的类型
     * @return 提取的声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从 Token 中提取所有声明。
     *
     * @param token JWT Token
     * @return 所有声明
     */
    private Claims extractAllClaims(String token)
    {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * 检查 Token 是否过期。
     *
     * @param token JWT Token
     * @return 如果 Token 已过期则为 true，否则为 false
     */
    private Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 验证 Token 是否有效。
     *
     * @param token       JWT Token
     * @param userDetails 用户详情
     * @return 如果 Token 有效则为 true，否则为 false
     */
    public Boolean validateToken(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 生成 Token。
     *
     * @param userDetails 用户详情
     * @return 生成的 JWT Token
     */
    public String generateToken(UserDetails userDetails)
    {
        Map<String, Object> claims = new HashMap<>();
        // 可以将用户角色等信息添加到 claims 中
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 创建 Token。
     *
     * @param claims  声明
     * @param subject 主题（通常是用户名）
     * @return 创建的 JWT Token
     */
    private String createToken(Map<String, Object> claims, String subject)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 获取签名密钥。
     *
     * @return 签名密钥
     */
    private Key getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
