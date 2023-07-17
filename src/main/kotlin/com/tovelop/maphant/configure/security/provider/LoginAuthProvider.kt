package com.tovelop.maphant.configure.security.provider

import com.tovelop.maphant.configure.security.TokenRepository
import com.tovelop.maphant.configure.security.UserDataService
import com.tovelop.maphant.configure.security.token.LoginAuthToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class LoginAuthProvider(
    val userDataService: UserDataService,
    val tokenRepository: TokenRepository,
)
    : AuthenticationProvider{
    override fun authenticate(authentication: Authentication?): Authentication {
        val usernamePasswordAuthenticationToken = authentication as LoginAuthToken
        val userData = userDataService.loadUserByUsername(usernamePasswordAuthenticationToken.name)

        if(userData.password != usernamePasswordAuthenticationToken.credentials) {
            throw BadCredentialsException("Invalid password")
        }

        val auth = tokenRepository.generateToken(userData)
        return LoginAuthToken(auth.first, auth.second, userData)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        if(authentication == null) return false

        return authentication == LoginAuthToken::class.java
    }

}