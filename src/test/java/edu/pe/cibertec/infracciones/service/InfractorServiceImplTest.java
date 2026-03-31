package edu.pe.cibertec.infracciones.service;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias-InfractorService")
public class InfractorServiceImplTest {
    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    InfractorServiceImpl infractorService;

    @Test
    @DisplayName("Calcular  deuda sumando multas pendientes y vencidas (15% cargo)")
    void givenInfractorConMultas_whenCalcularDeuda_thenRetornaDeudaCorrecta() {
        //Arange
        Long infractorId = 1L;
        Multa multaPendiente = new Multa();
        multaPendiente.setMonto(200.00);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);

        Multa multaVencida = new Multa();
        multaVencida.setMonto(300.00);
        multaVencida.setEstado(EstadoMulta.VENCIDA);

        List<Multa> multasEncontradas = List.of(multaPendiente, multaVencida);

        when(multaRepository.findByInfractor_IdAndEstadoIn(eq(infractorId), anyList()))
                .thenReturn(multasEncontradas);

        //Act
        Double resultado = infractorService.calcularDeuda(infractorId);

        //Assert
        assertEquals(545.00, resultado);
        verify(multaRepository, times(1)).findByInfractor_IdAndEstadoIn(eq(infractorId), anyList());
    }

    @Test
    @DisplayName("Designar Vehiculo si no tiene multas pendientes")
    void giveInfractorYVehiculoSinMultasPendientes_whenDesasingnar_thenVehiculoRemovido(){
        //Aranger
        Long infractorId = 1L;
        Long vehiculoId = 1L;

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(vehiculoId);
        Infractor infractor = new Infractor();
        infractor.setId(infractorId);

        infractor.setVehiculos(new ArrayList<>(List.of(vehiculo)));

        when(infractorRepository.findById(infractorId)).thenReturn(Optional.of(infractor));
        when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(vehiculo));
        when(multaRepository.findByVehiculo_IdAndEstado(vehiculoId,EstadoMulta.PENDIENTE))
                .thenReturn((new ArrayList<>()));
        //Act
        infractorService.designarVehiculo(infractorId, vehiculoId);

        //Assert
        assertEquals(0, infractor.getVehiculos().size());

        //Verify
        verify(infractorRepository, times(1)).save(infractor);

    }
}

