package me.dulce.gamesite.gamesite2.transportcontroller.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import me.dulce.gamesite.gamesite2.configuration.AppConfig;
import me.dulce.gamesite.gamesite2.utilservice.SpringService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class AuthServiceTest {

  private static final String VALID_TEST_FILE = "/json/UsersFile-test.json";
  private static final String INVALID_TEST_FILE = "/json/UsersFile-test-invalid.json";

  @Mock private SpringService springService;

  @Mock private AppConfig appConfig;

  @Autowired private ResourceLoader resourceLoader;

  @Test
  public void testConstructor_exceptionReading_exitsApp() {
    ResourceLoader mockResourceLoader = mock(ResourceLoader.class);
    when(mockResourceLoader.getResource(any())).thenThrow(new RuntimeException("Fake Exception"));
    when(springService.springAppExit(100)).thenReturn(100);

    new AuthService(springService, appConfig, mockResourceLoader);

    verify(springService, times(1)).springAppExit(100);
  }

  @Test
  public void textConstructor_invalidFile_emptyMap() {
    ResourceLoader mockResourceLoader = mock(ResourceLoader.class);
    when(mockResourceLoader.getResource(any())).thenThrow(new RuntimeException("Fake Exception"));
    when(springService.springAppExit(100)).thenReturn(100);

    AuthService authService = new AuthService(springService, appConfig, mockResourceLoader);

    assertEquals(0, authService.getLoginToProfileMap().size());
  }

  @Test
  public void testValidateAuthCreds_success() {
    String login = "testDeveloper";
    String hashedPassword = DigestUtils.sha256Hex("testPassword");
    String expectedUuid = "bffdd16d-79ff-4432-ac27-a82b390909b6";
    mockAppConfig(VALID_TEST_FILE);
    AuthService authService = instantiateAuthService();

    Optional<UUID> actual = authService.validateAuthCreds(login, hashedPassword);

    assertTrue(actual.isPresent());
    assertEquals(expectedUuid, actual.get().toString());
  }

  @Test
  public void testValidateAuthCreds_blankLogin_emptyOptional() {
    String login = "    ";
    String hashedPassword = DigestUtils.sha256Hex("testPassword");
    mockAppConfig(VALID_TEST_FILE);
    AuthService authService = instantiateAuthService();

    Optional<UUID> actual = authService.validateAuthCreds(login, hashedPassword);

    assertTrue(actual.isEmpty());
  }

  @Test
  public void testValidateAuthCreds_blankPass_emptyOptional() {
    String login = "testDeveloper";
    mockAppConfig(VALID_TEST_FILE);
    AuthService authService = instantiateAuthService();

    Optional<UUID> actual = authService.validateAuthCreds(login, "  ");

    assertTrue(actual.isEmpty());
  }

  @Test
  public void testValidateAuthCreds_invalidLogin_emptyOptional() {
    String login = "badLogin";
    String hashedPassword = DigestUtils.sha256Hex("testPassword");
    mockAppConfig(VALID_TEST_FILE);
    AuthService authService = instantiateAuthService();

    Optional<UUID> actual = authService.validateAuthCreds(login, hashedPassword);

    assertTrue(actual.isEmpty());
  }

  @Test
  public void testValidateAuthCreds_invalidPassword_emptyOptional() {
    String login = "testDeveloper";
    String hashedPassword = DigestUtils.sha256Hex("testPasswordsadasdas");
    mockAppConfig(VALID_TEST_FILE);
    AuthService authService = instantiateAuthService();

    Optional<UUID> actual = authService.validateAuthCreds(login, hashedPassword);

    assertTrue(actual.isEmpty());
  }

  private AuthService instantiateAuthService() {
    return new AuthService(springService, appConfig, resourceLoader);
  }

  private void mockAppConfig(String location) {
    when(appConfig.getUsersFile()).thenReturn(location);
  }
}
