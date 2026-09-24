package fr.formation.openapi.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.formation.openapi.dto.*;
import fr.formation.openapi.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employés", description = "Consultation et gestion des employés")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Lister les employés", description = "Le département est un filtre facultatif.")
    public List<EmployeeResponse> findAll(
        @Parameter(description = "Département à filtrer", example = "Informatique") @RequestParam(required = false) String department) {
        return service.findAll(department);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un employé par identifiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employé trouvé"),
        @ApiResponse(responseCode = "404", description = "Employé introuvable", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public EmployeeResponse findById(@Parameter(example = "1") @PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un employé")
    @ApiResponse(responseCode = "201", description = "Employé créé")
    public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un employé")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un employé")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Employé supprimé"), @ApiResponse(responseCode = "404", description = "Employé introuvable")})
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
