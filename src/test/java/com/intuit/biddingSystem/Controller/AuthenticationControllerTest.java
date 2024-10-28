package com.intuit.biddingSystem.Controller;

import com.intuit.biddingSystem.controller.AuthenticationController;
import com.intuit.biddingSystem.dto.AuthenticationRequest;
import com.intuit.biddingSystem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AuthenticationRequest validRequest;
    private AuthenticationRequest invalidRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validRequest = new AuthenticationRequest();
        validRequest.setUsername("validUser");
        validRequest.setPassword("validPassword");

        invalidRequest = new AuthenticationRequest();
        invalidRequest.setUsername("invalidUser");
        invalidRequest.setPassword("invalidPassword");
    }

    @Test
    void testCreateAuthenticationToken_Success_WithAuthenticationObject() throws Exception {
        // Arrange
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth); // Return a mock Authentication object
        when(jwtUtil.generateToken(validRequest.getUsername())).thenReturn("mocked-jwt-token");

        // Act
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(validRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof java.util.Map);
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> body = (java.util.Map<String, String>) response.getBody();
        assertTrue(body.containsKey("token"));
        assertEquals("mocked-jwt-token", body.get("token"));

        // Verify interactions
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authTokenCaptor.capture());
        assertEquals(validRequest.getUsername(), authTokenCaptor.getValue().getPrincipal());
        assertEquals(validRequest.getPassword(), authTokenCaptor.getValue().getCredentials());
        verify(jwtUtil, times(1)).generateToken(validRequest.getUsername());
    }

    @Test
    void testCreateAuthenticationToken_Success_WithDifferentUsernames() throws Exception {
        // Arrange
        AuthenticationRequest[] requests = {
                new AuthenticationRequestBuilder().withUsername("user123").withPassword("pass123").build(),
                new AuthenticationRequestBuilder().withUsername("user!@#").withPassword("pass!@#").build(),
                new AuthenticationRequestBuilder().withUsername("u".repeat(50)).withPassword("p".repeat(50)).build()
        };

        for (AuthenticationRequest request : requests) {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mock(Authentication.class));
            when(jwtUtil.generateToken(request.getUsername())).thenReturn("mocked-jwt-token-" + request.getUsername());

            // Act
            ResponseEntity<?> response = authenticationController.createAuthenticationToken(request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody() instanceof java.util.Map);
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> body = (java.util.Map<String, String>) response.getBody();
            assertTrue(body.containsKey("token"));
            assertEquals("mocked-jwt-token-" + request.getUsername(), body.get("token"));

            // Reset mocks for the next iteration
            reset(authenticationManager, jwtUtil);
        }
    }

    @Test
    void testCreateAuthenticationToken_BadCredentialsException() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(invalidRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Incorrect username or password", response.getBody());

        // Verify interactions
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authTokenCaptor.capture());
        assertEquals(invalidRequest.getUsername(), authTokenCaptor.getValue().getPrincipal());
        assertEquals(invalidRequest.getPassword(), authTokenCaptor.getValue().getCredentials());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testCreateAuthenticationToken_AuthenticationException() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new LockedException("User account is locked"));

        // Act
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(invalidRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authentication failed", response.getBody());

        // Verify interactions
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authTokenCaptor.capture());
        assertEquals(invalidRequest.getUsername(), authTokenCaptor.getValue().getPrincipal());
        assertEquals(invalidRequest.getPassword(), authTokenCaptor.getValue().getCredentials());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testCreateAuthenticationToken_JwtGenerationFailure() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class)); // Simulate successful authentication
        when(jwtUtil.generateToken(validRequest.getUsername())).thenThrow(new RuntimeException("JWT generation error"));

        // Act
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(validRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody());

        // Verify interactions
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authTokenCaptor.capture());
        assertEquals(validRequest.getUsername(), authTokenCaptor.getValue().getPrincipal());
        assertEquals(validRequest.getPassword(), authTokenCaptor.getValue().getCredentials());
        verify(jwtUtil, times(1)).generateToken(validRequest.getUsername());
    }

    @Test
    void testCreateAuthenticationToken_Exception() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<?> response = authenticationController.createAuthenticationToken(validRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody());

        // Verify interactions
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager, times(1)).authenticate(authTokenCaptor.capture());
        assertEquals(validRequest.getUsername(), authTokenCaptor.getValue().getPrincipal());
        assertEquals(validRequest.getPassword(), authTokenCaptor.getValue().getCredentials());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    // Utility builder for creating AuthenticationRequest instances with varying data
    static class AuthenticationRequestBuilder {
        private String username;
        private String password;

        AuthenticationRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        AuthenticationRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        AuthenticationRequest build() {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setUsername(username);
            request.setPassword(password);
            return request;
        }
    }
}
