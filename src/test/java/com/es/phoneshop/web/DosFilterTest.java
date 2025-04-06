package com.es.phoneshop.web;

import com.es.phoneshop.security.DefaultDosProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Spy
    private DefaultDosProtectionService dosProtectionService;

    @InjectMocks
    private DosFilter dosFilter;


    @Test
    public void doFilter_continuesFilterChain() throws IOException, ServletException {
        String ipAddress = "127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        doReturn(true).when(dosProtectionService).isAllowed(ipAddress);

        dosFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    public void doFilter_setsStatus429() throws IOException, ServletException {
        String ipAddress = "127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        doReturn(false).when(dosProtectionService).isAllowed(ipAddress);

        dosFilter.doFilter(request, response, chain);

        verify(response).setStatus(429);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void doFilter_dosProtectionService() {
        String ipAddress = "127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ipAddress);

        dosFilter.doFilter(request, response, chain);

        verify(dosProtectionService).isAllowed(ipAddress);
    }

    @Test
    public void doFilter_throwException() throws ServletException, IOException {
        doThrow(new RuntimeException("test")).when(chain).doFilter(request, response);
        String ipAddress = "127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ipAddress);

        dosFilter.doFilter(request, response, chain);

        verify(response).setStatus(404);
    }
}
