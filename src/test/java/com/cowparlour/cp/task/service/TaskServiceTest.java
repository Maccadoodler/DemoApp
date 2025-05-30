package com.cowparlour.cp.task.service;

import com.cowparlour.cp.task.dto.Average;
import com.cowparlour.cp.task.dto.TaskTime;
import com.cowparlour.cp.task.repository.DataStore;
import com.cowparlour.cp.task.repository.TaskMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    DataStore dataStore = mock(DataStore.class);


    @Test
    public void testAverage() {
        TaskService service = new TaskService(dataStore);
        TaskMetric row = new TaskMetric(1L, "aaa", BigInteger.TEN, BigInteger.TWO);
        when(dataStore.findByTask(any())).thenReturn(Optional.of(row));

        Optional<Average> result = service.getAverage("aaa");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().averageDuration(), BigInteger.valueOf(5L));
    }

    @Test
    public void testAverage_Empty() {
        TaskService service = new TaskService(dataStore);

        when(dataStore.findByTask(any())).thenReturn(Optional.empty());

        Optional<Average> result = service.getAverage("aaa");
        Assertions.assertFalse(result.isPresent());

    }

    @Test
    public void testAverage_Zero() {
        TaskService service = new TaskService(dataStore);
        TaskMetric row = new TaskMetric(1L, "aaa", BigInteger.TEN, BigInteger.ZERO);
        when(dataStore.findByTask(any())).thenReturn(Optional.of(row));
        Optional<Average> result = service.getAverage("aaa");
        Assertions.assertFalse(result.isPresent());

    }


    @Test
    public void testAverage_Problem() {
        TaskService service = new TaskService(dataStore);
        when(dataStore.findByTask(any())).thenThrow(new RuntimeException());
        Assertions.assertThrows(ServiceFailure.class, () -> service.getAverage("aaa"));
    }


    @Test
    public void testUpdateTask_New() {
        TaskService service = new TaskService(dataStore);
        TaskTime dto = new TaskTime("aaa", BigInteger.TEN);
        when(dataStore.findByTask(any())).thenReturn(Optional.empty());

        service.updateTask(dto);

        ArgumentCaptor<TaskMetric> captor = ArgumentCaptor.forClass(TaskMetric.class);
        verify(dataStore).save(captor.capture());

        TaskMetric metric = captor.getValue();
        assertEquals("aaa", metric.task());
        assertEquals(BigInteger.ONE, metric.count());
        assertEquals(BigInteger.TEN, metric.totalTime());
        assertNull(metric.id());

    }

    @Test
    public void testUpdateTask_Existing() {
        TaskService service = new TaskService(dataStore);
        Optional<TaskMetric> existing =
                Optional.of(new TaskMetric(1L, "aaa", BigInteger.TEN, BigInteger.ONE));

        TaskTime dto = new TaskTime("aaa", BigInteger.valueOf(20L));
        when(dataStore.findByTask(any())).thenReturn(existing);

        service.updateTask(dto);

        ArgumentCaptor<TaskMetric> captor = ArgumentCaptor.forClass(TaskMetric.class);
        verify(dataStore).save(captor.capture());

        TaskMetric metric = captor.getValue();
        assertEquals("aaa", metric.task());
        assertEquals(BigInteger.TWO, metric.count());
        assertEquals(BigInteger.valueOf(30L), metric.totalTime());
        assertEquals(1L, metric.id());

    }

    @Test
    public void testUpdateTask_Error() {
        TaskService service = new TaskService(dataStore);

        TaskTime dto = new TaskTime("aaa", BigInteger.valueOf(20L));
        when(dataStore.findByTask(any())).thenThrow(new RuntimeException("Howdy"));

        Exception exception = assertThrows(ServiceFailure.class, () -> {
                    service.updateTask(dto);
                });
        assertEquals("Problem with updating Datastore", exception.getMessage());
    }

}
