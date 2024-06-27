package BackEndC2.ClinicaOdontologica.controller;

import BackEndC2.ClinicaOdontologica.entity.Domicilio;
import BackEndC2.ClinicaOdontologica.entity.Odontologo;
import BackEndC2.ClinicaOdontologica.entity.Paciente;
import BackEndC2.ClinicaOdontologica.entity.Turno;
import BackEndC2.ClinicaOdontologica.service.OdontologoService;
import BackEndC2.ClinicaOdontologica.service.PacienteService;
import BackEndC2.ClinicaOdontologica.service.TurnoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TurnoControllerTest {
    @Autowired
    private TurnoService turnoService;
    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private OdontologoService odontologoService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void listarTodosLosTurnos() throws Exception {
        Paciente pacienteGuardado = pacienteService.guardarPaciente(new Paciente("Jorgito", "Pereyra", "111111", LocalDate.of(2024, 6, 19), new Domicilio("Calle falsa", 123, "La Rioja", "Argentina"), "jorgito@digitalhouse.com"));

        Odontologo odontologoGuardado = odontologoService.guardarOdontologo(new Odontologo("NS1", "David", "Rios"));

        Turno turnoGuardado;
        turnoGuardado = turnoService.guardarTurno(new Turno(pacienteGuardado, odontologoGuardado, LocalDateTime.of(2024, 06, 24, 12, 23, 00)));

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/turnos")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertFalse(respuesta.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void guardarTurno() throws Exception {
        Domicilio domicilioPaciente = new Domicilio("Calle Falsa", 123, "CDMX", "MEX");
        Paciente paciente = new Paciente("Juan", "Perez", "12345678", LocalDate.now(), domicilioPaciente, "juanperez1@example.com");

        pacienteService.guardarPaciente(paciente);

        Odontologo odontologo = new Odontologo("NS1", "David", "Rios");

        odontologoService.guardarOdontologo(odontologo);

        Turno turno = new Turno(paciente, odontologo, LocalDateTime.of(2024, 06, 15, 06, 44, 00));


        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.post("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turno)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("\"id\""));
    }



    @Test
    public void actualizarTurno() throws Exception {
        Domicilio domicilioPaciente = new Domicilio("Calle Falsa 2", 123, "CDMX", "MEX");
        Paciente paciente = new Paciente("Carol", "Perez", "123347", LocalDate.now(), domicilioPaciente, "carp@mail.com");

        pacienteService.guardarPaciente(paciente);

        Odontologo odontologo = new Odontologo("NS5", "Carlos", "Vazquez");

        odontologoService.guardarOdontologo(odontologo);

        LocalDateTime fechaHoraOriginal = LocalDateTime.of(2024, 06, 15, 06, 44, 00);
        Turno turnoInicial = new Turno(paciente, odontologo, fechaHoraOriginal);

        Turno turnoGuardado = turnoService.guardarTurno(turnoInicial);

        LocalDateTime nuevaFechaHora = LocalDateTime.of(2024, 06, 16, 10, 30, 00);

        turnoGuardado.setFechaHoraCita(nuevaFechaHora);


        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.put("/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoGuardado)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("Turno actualizado"));

        Optional<Turno> turnoActualizado = turnoService.buscarPorID(turnoGuardado.getId());

        assertTrue(turnoActualizado.isPresent());
        assertTrue(turnoActualizado.get().getFechaHoraCita().isEqual(nuevaFechaHora));
    }



    @Test
    public void eliminarTurno() throws Exception {
        Domicilio domicilioPaciente = new Domicilio("Calle Falsa 3", 123, "BA", "ARG");
        Paciente paciente = new Paciente("Juan", "Perez", "12345678", LocalDate.now(), domicilioPaciente, "juanperez2@example.com");

        pacienteService.guardarPaciente(paciente);

        Odontologo odontologo = new Odontologo("NS1", "David", "Rios");

        odontologoService.guardarOdontologo(odontologo);


        LocalDateTime fechaHora = LocalDateTime.of(2024, 06, 26, 06, 27, 00);
        Turno turno = new Turno(paciente, odontologo, fechaHora);

        Turno turnoGuardado = turnoService.guardarTurno(turno);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.delete("/turnos/eliminar/{id}", turnoGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("turno eliminado con exito"));

        assertFalse(turnoService.buscarPorID(turnoGuardado.getId()).isPresent());
    }


    @Test
    public void buscarTurnoPorId() throws Exception {
        Domicilio domicilioPaciente = new Domicilio("Calle Falsa 4", 123, "cdmx", "MEX");
        Paciente paciente = new Paciente("Juan", "Perez", "12345678", LocalDate.now(), domicilioPaciente, "juanperez5@example.com");

        pacienteService.guardarPaciente(paciente);

        Odontologo odontologo = new Odontologo("NS1", "David", "Rios");

        odontologoService.guardarOdontologo(odontologo);

        LocalDateTime fechaHora = LocalDateTime.of(2024, 06, 15, 06, 44, 00);
        Turno turno = new Turno(paciente, odontologo, fechaHora);

        Turno turnoGuardado = turnoService.guardarTurno(turno);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/turnos/{id}", turnoGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Turno turnoEncontrado = objectMapper.readValue(respuesta.getResponse().getContentAsString(), Turno.class);

        assertEquals(turnoGuardado.getId(), turnoEncontrado.getId());
        assertEquals(turnoGuardado.getFechaHoraCita(), turnoEncontrado.getFechaHoraCita());
        assertEquals(turnoGuardado.getPaciente().getNombre(), turnoEncontrado.getPaciente().getNombre());
        assertEquals(turnoGuardado.getOdontologo().getNombre(), turnoEncontrado.getOdontologo().getNombre());
    }
}