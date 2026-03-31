package edu.pe.cibertec.infracciones.service;


import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Prueba MultaService")
public class MultaServiceImplTest {
    @Mock
    private MultaRepository multaRepository;
    @Mock
    private InfractorRepository infractorRepository;
    @InjectMocks
    private MultaServiceImpl multaService;

    @Test
    @DisplayName("Transferir Multa si el infractor B no esta bloqueado y tiene el vehicula")
    void givenMultaPendienteNuevoInfractorValido_whenTransferir_thenMultaAsignadaAB(){
        //Arrage
        Long multaId= 1L;
        Long infractorBId = 2L;
        Vehiculo vehiculoCompartido = new Vehiculo();
        vehiculoCompartido.setId(10L);

        Infractor infractorA = new Infractor();
        infractorA.setId(1L);

        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setInfractor(infractorA);
        multa.setVehiculo(vehiculoCompartido);
        Infractor infractorB = new Infractor();
        infractorB.setId(infractorBId);
        infractorB.setBloqueado(false);
        infractorB.setVehiculos(new ArrayList<>(List.of(vehiculoCompartido)));

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(infractorBId)).thenReturn(Optional.of(infractorB));
        //Act
        multaService.transferirMulta(multaId, infractorBId);
        //Assert
        assertEquals(infractorB, multa.getInfractor());
        verify(multaRepository, times(1)).save(multa);

    }
}
