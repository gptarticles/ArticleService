package me.zedaster.articleservice.service;

import jakarta.validation.ConstraintViolationException;
import me.zedaster.articleservice.entity.Creator;
import me.zedaster.articleservice.repository.CreatorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link CreatorService}.
 */
@SpringBootTest(classes = {CreatorService.class, ValidationAutoConfiguration.class})
public class CreatorServiceTest {

    private static final String INCORRECT_USER_ID = "User id must be bigger than zero!";
    private static final String NULL_USER_NAME = "Username must be not null!";

    private static final String INCORRECT_USER_NAME = "Username does not meet the requirements!";

    @Autowired
    private CreatorService creatorService;

    @MockBean
    private CreatorRepository creatorRepository;

    /**
     * Checks if a creator is added
     */
    @Test
    public void addIfNotExists() {
        Mockito.when(creatorRepository.existsById(1L)).thenReturn(false);
        creatorService.addIfNotExist(1L, "john");
        Mockito.verify(creatorRepository).save(new Creator(1L, "john"));
    }

    /**
     * Checks if a creator is not added if he already exists
     */
    @Test
    public void tryAddIfExists() {
        Mockito.when(creatorRepository.existsById(1L)).thenReturn(true);
        creatorService.addIfNotExist(1L, "john");
        Mockito.verify(creatorRepository, Mockito.times(1)).existsById(1L);
        Mockito.verify(creatorRepository, Mockito.never()).save(Mockito.any());
    }

    /**
     * Checks if adding a creator with a wrong ID throws an exception
     */
    @Test
    public void addWithWrongId() {
        for (long id : new long[]{0, -1}) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> creatorService.addIfNotExist(id, "john"));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals(INCORRECT_USER_ID,
                    ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

    /**
     * Checks if adding a creator with a null username throws an exception
     */
    @Test
    public void addWithNullUsername() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> creatorService.addIfNotExist(1, null));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals(NULL_USER_NAME,
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if adding a creator with a wrong username throws an exception
     */
    @Test
    public void addWithWrongUsername() {
        for (String username : new String[]{"", "z", "я".repeat(3), "c".repeat(33), "."}) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> creatorService.addIfNotExist(1L, username));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals(INCORRECT_USER_NAME, ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

    /**
     * Checks if a creator is updated
     * @throws CreatorServiceException This exception should not be thrown
     */
    @Test
    public void updateCreator() throws CreatorServiceException {
        Mockito.when(creatorRepository.existsById(1L)).thenReturn(true);
        creatorService.updateCreator(1L, "billy");
        Mockito.verify(creatorRepository).save(new Creator(1L, "billy"));
    }

    /**
     * Checks if updating a non-existing creator throws an exception
     */
    @Test
    public void updateNonExistingCreator() {
        Mockito.when(creatorRepository.existsById(1L)).thenReturn(false);
        CreatorServiceException ex = Assertions.assertThrows(CreatorServiceException.class,
                () -> creatorService.updateCreator(1L, "billy"));
        Mockito.verify(creatorRepository, Mockito.never()).save(Mockito.any());
        Assertions.assertEquals("Creator with the ID doesn't exist!", ex.getMessage());
    }

    /**
     * Checks if updating a creator with a wrong ID throws an exception
     */
    @Test
    public void updateCreatorWithWrongId() {
        for (long id : new long[]{0, -1}) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> creatorService.updateCreator(id, "john"));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals(INCORRECT_USER_ID,
                    ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

    /**
     * Checks if updating a creator with a null username throws an exception
     */
    @Test
    public void updateCreatorWithNullUsername() {
        ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                () -> creatorService.updateCreator(1, null));
        Assertions.assertEquals(1, ex.getConstraintViolations().size());
        Assertions.assertEquals(NULL_USER_NAME,
                ex.getConstraintViolations().stream().findFirst().get().getMessage());
    }

    /**
     * Checks if updating a creator with a wrong username throws an exception
     */
    @Test
    public void updateCreatorWithWrongUsername() {
        for (String username : new String[]{"", "z", "я".repeat(3), "c".repeat(33), "."}) {
            ConstraintViolationException ex = Assertions.assertThrows(ConstraintViolationException.class,
                    () -> creatorService.updateCreator(1L, username));
            Assertions.assertEquals(1, ex.getConstraintViolations().size());
            Assertions.assertEquals(INCORRECT_USER_NAME, ex.getConstraintViolations().stream().findFirst().get().getMessage());
        }
    }

}
