package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

  @Mock private UserRepository repository;

  @Mock private CustomUserUtil userUtil;

  private UserEntity user = new UserEntity();

  @BeforeEach
  void setUp() {
    user = UserFactory.createUserEntity();
  }

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
    Mockito.when(userUtil.getLoggedUsername()).thenReturn(user.getUsername());
    Mockito.when(repository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

    Assertions.assertDoesNotThrow(
        () -> {
          UserEntity result = service.authenticated();
          Assertions.assertNotNull(result);
          Assertions.assertEquals(user.getUsername(), result.getUsername());
        });

    Mockito.verify(userUtil).getLoggedUsername();
    Mockito.verify(repository).findByUsername(user.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
    String username = "Jon Doe";
    Mockito.when(userUtil.getLoggedUsername()).thenReturn(username);
    Mockito.when(repository.findByUsername(username)).thenThrow(UsernameNotFoundException.class);

    Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());

    Mockito.verify(repository).findByUsername(username);
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
    List<UserDetailsProjection> result =
        UserDetailsFactory.createCustomAdminClientUser(user.getUsername());
    Mockito.when(repository.searchUserAndRolesByUsername(user.getUsername())).thenReturn(result);

    Assertions.assertDoesNotThrow(
        () -> {
          var userDetails = service.loadUserByUsername(user.getUsername());
          Assertions.assertNotNull(userDetails);
          Assertions.assertEquals(user.getUsername(), userDetails.getUsername());
          Assertions.assertEquals(result.getFirst().getPassword(), userDetails.getPassword());
        });

    Mockito.verify(repository).searchUserAndRolesByUsername(user.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
    Mockito.when(repository.searchUserAndRolesByUsername(user.getUsername())).thenReturn(List.of());

    Assertions.assertThrows(
        UsernameNotFoundException.class, () -> service.loadUserByUsername(user.getUsername()));

    Mockito.verify(repository).searchUserAndRolesByUsername(user.getUsername());
	}
}
