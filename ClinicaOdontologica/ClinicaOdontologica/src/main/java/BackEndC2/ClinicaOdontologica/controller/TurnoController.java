package BackEndC2.ClinicaOdontologica.controller;

import BackEndC2.ClinicaOdontologica.entity.Odontologo;
import BackEndC2.ClinicaOdontologica.entity.Paciente;
import BackEndC2.ClinicaOdontologica.entity.Turno;
import BackEndC2.ClinicaOdontologica.exception.ResourceNotFoundException;
import BackEndC2.ClinicaOdontologica.service.OdontologoService;
import BackEndC2.ClinicaOdontologica.service.PacienteService;
import BackEndC2.ClinicaOdontologica.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private OdontologoService odontologoService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @GetMapping
    public ResponseEntity<List<Turno>> buscarTodos() {
        return ResponseEntity.ok(turnoService.buscarTodos());
    }

    @PostMapping //nos permite crear o registrar un turno
    public ResponseEntity<Turno> registrarUnTurno(@RequestBody Turno turno) throws ResourceNotFoundException {
        // Validamos si el paciente y el odontologo existen
        Optional<Paciente> pacienteBuscado = pacienteService.buscarPorID(turno.getPaciente().getId());
        Optional<Odontologo> odontologoBuscado = odontologoService.buscarPorID(turno.getOdontologo().getId());
        if (!pacienteBuscado.isPresent() && !odontologoBuscado.isPresent()) {
            throw new ResourceNotFoundException("No se encontraron paciente con id: " + turno.getPaciente().getId() + " ni odontólogo con id: " + turno.getOdontologo().getId());
        } else if (!pacienteBuscado.isPresent()) {
            throw new ResourceNotFoundException("No se encontró paciente con id: " + turno.getPaciente().getId());
        } else if (!odontologoBuscado.isPresent()) {
            throw new ResourceNotFoundException("No se encontró odontólogo con id: " + turno.getOdontologo().getId());
        }

        // Si ambos existen, procesamos la fecha y guardamos el turno
        turno.setFechaHoraCita(LocalDateTime.parse(turno.getFechaHoraCita().toString(), formatter));
        return ResponseEntity.ok(turnoService.guardarTurno(turno));
    }

    @PutMapping
    public ResponseEntity<String> actualizarTurno(@RequestBody Turno turno) throws ResourceNotFoundException {
        // Primero verificamos si el turno existe
        Optional<Turno> turnoBuscado = turnoService.buscarPorID(turno.getId());
        if (turnoBuscado.isPresent()) {
            // Verificamos si el paciente y el odontólogo existen
            Optional<Paciente> pacienteBuscado = pacienteService.buscarPorID(turno.getPaciente().getId());
            Optional<Odontologo> odontologoBuscado = odontologoService.buscarPorID(turno.getOdontologo().getId());
            if (!pacienteBuscado.isPresent() && !odontologoBuscado.isPresent()) {
                throw new ResourceNotFoundException("No se encontraron paciente con id: " + turno.getPaciente().getId() + " ni odontólogo con id: " + turno.getOdontologo().getId());
            } else if (!pacienteBuscado.isPresent()) {
                throw new ResourceNotFoundException("No se encontró paciente con id: " + turno.getPaciente().getId());
            } else if (!odontologoBuscado.isPresent()) {
                throw new ResourceNotFoundException("No se encontró odontólogo con id: " + turno.getOdontologo().getId());
            }

            // Si el turno, el paciente y el odontólogo existen, actualizamos los datos del turno
            turno.setFechaHoraCita(LocalDateTime.parse(turno.getFechaHoraCita().toString(), formatter));
            turnoService.actualizarTurno(turno);
            return ResponseEntity.ok("Turno actualizado");
        } else {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Turno no encontrado");
            //aca lanzamos la exception
            throw new ResourceNotFoundException("No se encontró turno con id: " + turno.getId());
        }
    }

    @GetMapping("/{id}") //Buscar Turno por Id
    public ResponseEntity<Turno> buscarTurnoPorId(@PathVariable Long id) throws ResourceNotFoundException {

        Optional<Turno> turnoBuscado = turnoService.buscarPorID(id);
        if (turnoBuscado.isPresent()) {
            return ResponseEntity.ok(turnoBuscado.get());
        } else {
            //return ResponseEntity.notFound().build();
            //aca lanzamos la exception
            throw new ResourceNotFoundException("No se encontró turno con id: " + id);
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTurno(@PathVariable Long id) throws ResourceNotFoundException {
        Optional<Turno> turnoBuscado = turnoService.buscarPorID(id);
        if (turnoBuscado.isPresent()) {
            turnoService.eliminarTurno(id);
            return ResponseEntity.ok("turno eliminado con exito");
        } else {
            // return ResponseEntity.badRequest().body("turno no encontrado");
            //aca lanzamos la exception
            throw new ResourceNotFoundException("No existe turno con id: " + id);
        }
    }
}
