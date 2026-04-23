package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

  @Mock
  private MovieRepository repository;

  private long existingId;

  private long nonExistingId;

  private long inconsistentId;

  private MovieEntity movie = new MovieEntity();

  @BeforeEach
  void setUp() {
    movie = MovieFactory.createMovieEntity();
    existingId = 1L;
    nonExistingId = 2L;
    inconsistentId = 3L;
  }
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
    Page<MovieEntity> page = new PageImpl<>(List.of(movie));
    Mockito.when(repository.searchByTitle(Mockito.anyString(), Mockito.any())).thenReturn(page);

    Assertions.assertDoesNotThrow(() -> service.findAll("any", null));
    Mockito.verify(repository).searchByTitle(Mockito.anyString(), Mockito.any());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
    Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(movie));

    Assertions.assertDoesNotThrow(() -> service.findById(existingId));
    Mockito.verify(repository).findById(existingId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

    Mockito.verify(repository).findById(nonExistingId);
  }

	@Test
	public void insertShouldReturnMovieDTO() {
    Mockito.when(repository.save(Mockito.any())).thenReturn(movie);

    Assertions.assertDoesNotThrow(() -> service.insert(MovieFactory.createMovieDTO()));
    Mockito.verify(repository).save(Mockito.any());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
    Mockito.when(repository.getReferenceById(existingId)).thenReturn(movie);
    Mockito.when(repository.save(Mockito.any())).thenReturn(movie);

    Assertions.assertDoesNotThrow(() -> service.update(existingId, MovieFactory.createMovieDTO()));
    Mockito.verify(repository).getReferenceById(existingId);
    Mockito.verify(repository).save(Mockito.any());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    Mockito.when(repository.getReferenceById(nonExistingId))
        .thenThrow(new EntityNotFoundException("Entity not found"));

    Assertions.assertThrows(
        ResourceNotFoundException.class,
        () -> service.update(nonExistingId, MovieFactory.createMovieDTO()));
    Mockito.verify(repository).getReferenceById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
    Mockito.when(repository.existsById(existingId)).thenReturn(true);

    Assertions.assertDoesNotThrow(() -> service.delete(existingId));
    Mockito.verify(repository).existsById(existingId);
    Mockito.verify(repository).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

    Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));

    Mockito.verify(repository).existsById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
    Mockito.when(repository.existsById(inconsistentId)).thenReturn(true);
    Mockito.doThrow(DatabaseException.class).when(repository).deleteById(inconsistentId);
    Assertions.assertThrows(DatabaseException.class, () -> service.delete(inconsistentId));
    Mockito.verify(repository).existsById(inconsistentId);
    Mockito.verify(repository).deleteById(inconsistentId);
	}
}
