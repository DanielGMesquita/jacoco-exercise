package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

  @Mock private ScoreRepository repository;

  @Mock private MovieRepository movieRepository;

  @Mock private UserService userService;

  private ScoreEntity score = new ScoreEntity();
  private MovieEntity movie = new MovieEntity();
  private UserEntity user = new UserEntity();
  private ScoreDTO scoreDTO;
  private long existingId;
  private long nonExistingId;

  @BeforeEach
  void setUp() {
    score = ScoreFactory.createScoreEntity();
    movie = MovieFactory.createMovieEntity();
    user = UserFactory.createUserEntity();
    scoreDTO = ScoreFactory.createScoreDTO();
    existingId = 1L;
    nonExistingId = 2L;
  }

  @Test
  public void saveScoreShouldReturnMovieDTO() {
    Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
    Mockito.when(repository.saveAndFlush(Mockito.any())).thenReturn(score);
    Mockito.when(userService.authenticated()).thenReturn(user);
    Mockito.when(movieRepository.save(Mockito.any())).thenReturn(movie);

    Assertions.assertDoesNotThrow(() -> service.saveScore(scoreDTO));

    Mockito.verify(repository).saveAndFlush(Mockito.any());
    Mockito.verify(movieRepository).findById(existingId);
    Mockito.verify(userService).authenticated();
    Mockito.verify(movieRepository).save(Mockito.any());
  }

	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
    Mockito.when(movieRepository.findById(nonExistingId))
        .thenThrow(ResourceNotFoundException.class);
    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> service.saveScore(new ScoreDTO(nonExistingId, scoreDTO.getScore())));
    Mockito.verify(movieRepository).findById(nonExistingId);
	}
}
