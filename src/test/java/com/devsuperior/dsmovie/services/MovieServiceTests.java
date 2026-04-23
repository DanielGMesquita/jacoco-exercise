package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.tests.MovieFactory;
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

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

  @Mock
  private MovieRepository repository;

  private long existingId;

  private MovieEntity movie = new MovieEntity();

  @BeforeEach
  void setUp() {
    movie = MovieFactory.createMovieEntity();
    existingId = 1L;
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
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
	}
}
