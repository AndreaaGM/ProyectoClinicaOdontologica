package BackEndC2.ClinicaOdontologica.controller;

import BackEndC2.ClinicaOdontologica.entity.Domicilio;
import BackEndC2.ClinicaOdontologica.entity.Paciente;
import BackEndC2.ClinicaOdontologica.service.PacienteService;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void listarTodosLosPacientes() throws Exception {
        Domicilio domicilio1 = new Domicilio("Calle Falsa", 1111, "EDOMEX", "MEX");
        Paciente paciente1 = new Paciente("Andrea", "Gallegos", "12345678", LocalDate.now(), domicilio1, "agm@mail.com");

        pacienteService.guardarPaciente(paciente1);

        Domicilio domicilio2 = new Domicilio("Calle falsa2", 2222, "CDMX", "MEX");
        Paciente paciente2 = new Paciente("Monica", "Moreno", "2024001", LocalDate.now(), domicilio2, "mgm@mail.com");

        pacienteService.guardarPaciente(paciente2);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/pacientes")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertFalse(respuesta.getResponse().getContentAsString().isEmpty());
    }


    @Test
    public void guardarPaciente() throws Exception {
        Domicilio domicilio = new Domicilio("Calle 1", 234, "loreto", "MEX");
        Paciente paciente = new Paciente("Sonia", "Perez", "176546", LocalDate.now(), domicilio, "sp@mail.com");

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paciente)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        assertFalse(respuesta.getResponse().getContentAsString().isEmpty());
    }


    @Test
    public void actualizarPaciente() throws Exception {
        Domicilio domicilio = new Domicilio("Calle 2", 456, "ciudad2", "Mex");
        Paciente pacienteExistente = new Paciente("Diego", "Flores", "123487", LocalDate.now(), domicilio, "diflo@mail.com");
        Paciente pacienteGuardado = pacienteService.guardarPaciente(pacienteExistente);
        Domicilio domicilioActualizado = new Domicilio("Calle 2", 456, "ciudad2", "Mex");
        Paciente pacienteActualizado = new Paciente(pacienteGuardado.getId(), "Diego", "Flores", "123487", LocalDate.now(), domicilioActualizado, "diflo@mail.com");

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pacienteActualizado)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("Paciente actualizado"));

        Paciente pacienteVerificado = pacienteService.buscarPorID(pacienteGuardado.getId()).orElse(null);
        assertTrue(pacienteVerificado != null && "Calle Actualizada".equals(pacienteVerificado.getDomicilio().getCalle()));
    }


    @Test
    public void buscarPacientePorId() throws Exception {
        Domicilio domicilio = new Domicilio("Calle 3", 3456, "ciudad2", "Colombia");
        Paciente paciente = new Paciente("Aby", "Rojas", "8765445", LocalDate.now(), domicilio, "abyred@mail.com");

        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/pacientes/{id}", pacienteGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Paciente pacienteEncontrado = objectMapper.readValue(respuesta.getResponse().getContentAsString(), Paciente.class);

        assertTrue(pacienteEncontrado != null);
        assertTrue(pacienteEncontrado.getId().equals(pacienteGuardado.getId()));
        assertTrue(pacienteEncontrado.getNombre().equals(pacienteGuardado.getNombre()));
        assertTrue(pacienteEncontrado.getApellido().equals(pacienteGuardado.getApellido()));
        assertTrue(pacienteEncontrado.getEmail().equals(pacienteGuardado.getEmail()));
    }


    @Test
    public void buscarPacientePorEmail() throws Exception {
        Domicilio domicilio = new Domicilio("Calle 4", 765, "ciudad 3", "CAN");
        Paciente paciente = new Paciente("Moisés", "Lambert", "201550474", LocalDate.now(), domicilio, "moilambert@mail.com");
        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);
        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.get("/pacientes/email/{email}", pacienteGuardado.getEmail())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Paciente pacienteEncontrado = objectMapper.readValue(respuesta.getResponse().getContentAsString(), Paciente.class);

        assertTrue(pacienteEncontrado != null);
        assertTrue(pacienteEncontrado.getEmail().equals(pacienteGuardado.getEmail()));
    }


    @Test
    public void eliminarPaciente() throws Exception {
        Domicilio domicilio = new Domicilio("Calle 5", 765, "ciudad 5", "Japon");
        Paciente paciente = new Paciente("Victor", "Torres", "38945575", LocalDate.now(), domicilio, "victor@main.com");

        Paciente pacienteGuardado = pacienteService.guardarPaciente(paciente);

        MvcResult respuesta = mockMvc.perform(MockMvcRequestBuilders.delete("/pacientes/eliminar/{id}", pacienteGuardado.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertTrue(respuesta.getResponse().getContentAsString().contains("paciente eliminado con exito"));
        assertFalse(pacienteService.buscarPorID(pacienteGuardado.getId()).isPresent());
    }
}