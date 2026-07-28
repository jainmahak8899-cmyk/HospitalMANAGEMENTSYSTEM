# Security Policy

## Supported Versions

The following versions of MediCare Hospital Management System are currently supported with security updates:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

We take the security of our application very seriously. If you discover a security vulnerability, please follow these guidelines:

1. **Do NOT publicly disclose** the vulnerability on GitHub issues or social media.
2. Email details of the vulnerability directly to security@medicareplus.com or contact the repository owner.
3. Include detailed steps to reproduce the issue, along with any relevant proof-of-concept code or logs.
4. You will receive an acknowledgment within 48 hours, followed by updates on the remediation timeline.

## Security Best Practices Applied

- **No Hardcoded Secrets**: Secrets and credentials are passed via environment configurations.
- **Salted Password Hashing**: Passwords are hashed with SHA-256 and unique 16-byte salts (`PasswordUtil.java`).
- **Parameterized SQL Queries**: All database DAO interactions use PreparedStatements to prevent SQL injection vulnerabilities.
- **Strict CORS Policy**: `CorsFilter` dynamically validates incoming `Origin` headers.
