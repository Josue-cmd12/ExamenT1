package edu.pe.cibertec.infracciones.service;


import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Prueba PagoService")
public class PagoServiceImplTest {
    @Mock
    private MultaRepository multaRepository;
    @Mock
    private PagoRepository pagoRepository;
    @InjectMocks
    private PagoServiceImpl pagoService;

    @Captor
    private ArgumentCaptor<Pago> pagoCaptor;

    @Test
    @DisplayName("Pago con descuento usando captor")
    void givenMultaReciente_whenProcesarPago_thenAplicaDescuentoGuarda(){
        //Arrange
        Long multaId= 1L;
        Multa multa= new Multa();
        multa.setId(multaId);
        multa.setMonto(1000.0);
        multa.setEstado(EstadoMulta.PENDIENTE);

        multa.setFechaEmision(LocalDate.now().minusDays(3));
        multa.setFechaVencimiento(LocalDate.now().plusDays(10));

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        //Act
        pagoService.procesarPago(multaId);
        //Assert
        verify(pagoRepository, times(1)).save(pagoCaptor.capture());
        Pago pagoGuardado=pagoCaptor.getValue();
        assertEquals(200.0, pagoGuardado.getDescuentoAplicado());
        assertEquals(800.0, pagoGuardado.getMontoPagado());
        assertEquals(0.0, pagoGuardado.getRecargo());
        assertEquals(EstadoMulta.PAGADA, multa.getEstado());
        verify(multaRepository, times(1)).save(multa);
    }
}
