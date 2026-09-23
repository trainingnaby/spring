package com.formation.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formation.domain.Duplicata;
import com.formation.dto.DuplicataDto;
import com.formation.repository.projection.DuplicataResumeProjection;
import com.formation.service.DuplicataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@Tag(name = "Duplicatas", description = "Services REST de génération et consultation des duplicatas d'impôts")
public class DuplicataControlleur {

    private final DuplicataService duplicataService;

    public DuplicataControlleur(DuplicataService duplicataService) {
        this.duplicataService = duplicataService;
    }

    @Operation(summary = "Lister tous les duplicatas", description = "Retourne tous les duplicatas triés du plus récent au plus ancien.")
    @ApiResponse(responseCode = "200", description = "Liste des duplicatas",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Duplicata.class))))
    @GetMapping("/duplicatas")
    public List<Duplicata> duplicatas() {
        return duplicataService.getDuplicatas();
    }

    @Operation(summary = "Visualiser un duplicata", description = "Recherche un duplicata par son identifiant technique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duplicata trouvé",
                    content = @Content(schema = @Schema(implementation = Duplicata.class))),
            @ApiResponse(responseCode = "404", description = "Duplicata introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/duplicatas/{id}")
    public Duplicata getDuplicata(
            @Parameter(description = "Identifiant du duplicata", example = "dup-demo-001")
            @PathVariable String id) {
        return duplicataService.getById(id);
    }

    @Operation(summary = "Créer un duplicata avec des paramètres", description = "Exemple historique du fil rouge : les données sont envoyées en paramètres de formulaire ou query string.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duplicata créé",
                    content = @Content(schema = @Schema(implementation = Duplicata.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur inconnu",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/duplicatas")
    public Duplicata createDuplicata(
            @Parameter(description = "Identifiant fiscal de l'utilisateur", example = "123456789")
            @RequestParam("user_id") @NotBlank String userId,
            @Parameter(description = "Montant du duplicata", example = "2500")
            @RequestParam @Min(1000) @Max(7000) Integer montant) {
        return duplicataService.createDuplicata(userId, montant);
    }

    @Operation(summary = "Créer un duplicata avec variables de chemin", description = "Variante pédagogique pour illustrer @PathVariable.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duplicata créé",
                    content = @Content(schema = @Schema(implementation = Duplicata.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/duplicatas/{userId}/{montant}")
    public Duplicata createDuplicata_path(
            @Parameter(description = "Identifiant fiscal", example = "123456789")
            @PathVariable @NotBlank String userId,
            @Parameter(description = "Montant", example = "2500")
            @PathVariable @Min(1000) @Max(7000) Integer montant) {
        return duplicataService.createDuplicata(userId, montant);
    }

    @Operation(summary = "Créer un duplicata avec un corps JSON", description = "Endpoint recommandé pour une API REST : les données sont envoyées dans un DTO JSON validé avec Bean Validation.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Données de génération du duplicata",
                    content = @Content(schema = @Schema(implementation = DuplicataDto.class),
                            examples = @ExampleObject(name = "Exemple valide", value = "{\n  \"user_id\": \"123456789\",\n  \"montant\": 2500\n}"))))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Duplicata créé",
                    content = @Content(schema = @Schema(implementation = Duplicata.class))),
            @ApiResponse(responseCode = "400", description = "Corps JSON invalide",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur inconnu",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/duplicatas_dto")
    public Duplicata createDuplicata_dto(@RequestBody @Valid DuplicataDto duplicataDto) {
        return duplicataService.createDuplicata(duplicataDto.getUserId(), duplicataDto.getMontant());
    }

    @Operation(summary = "Supprimer un duplicata", description = "Supprime un duplicata par son identifiant.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Duplicata supprimé"),
            @ApiResponse(responseCode = "404", description = "Duplicata introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/duplicatas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDuplicata(
            @Parameter(description = "Identifiant du duplicata", example = "dup-demo-001")
            @PathVariable String id) {
        duplicataService.deleteById(id);
    }

    @Operation(summary = "Rechercher par utilisateur", description = "Exemple de requête dérivée Spring Data JPA : findByUserId.")
    @GetMapping("/duplicatas/by-user/{userId}")
    public List<Duplicata> rechercherParUserId(@PathVariable String userId) {
        return duplicataService.rechercherParUserId(userId);
    }

    @Operation(summary = "Rechercher par intervalle de montant", description = "Exemple de requête dérivée avec Between et OrderBy.")
    @GetMapping("/duplicatas/by-montant")
    public List<Duplicata> rechercherParMontant(
            @RequestParam(defaultValue = "1000") int min,
            @RequestParam(defaultValue = "7000") int max) {
        return duplicataService.rechercherParMontantEntre(min, max);
    }

    @Operation(summary = "Recherche textuelle", description = "Exemple de requête dérivée contenant une chaîne, insensible à la casse.")
    @GetMapping("/duplicatas/search")
    public List<Duplicata> rechercherParUserIdContenant(@RequestParam(defaultValue = "") String q) {
        return duplicataService.rechercherParUserIdContenant(q);
    }

    @Operation(summary = "Recherche avec JPQL", description = "Exemple de requête @Query écrite en JPQL dans le repository.")
    @GetMapping("/duplicatas/jpql")
    public List<Duplicata> rechercherAvecJpql(@RequestParam(defaultValue = "3000") int min) {
        return duplicataService.rechercherAvecJpql(min);
    }

    @Operation(summary = "Lister les projections", description = "Exemple de projection Spring Data JPA : l'API renvoie seulement quelques champs.")
    @GetMapping("/duplicatas/projections")
    public List<DuplicataResumeProjection> listerProjections(@RequestParam(defaultValue = "1000") int min) {
        return duplicataService.listerResumes(min);
    }

    @Operation(summary = "Recherche paginée et triée", description = "Exemple de Pageable : page, taille, propriété de tri et sens de tri.")
    @GetMapping("/duplicatas/page")
    public Page<Duplicata> rechercherPagee(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Propriété de tri : createdAt, montant, userId", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Sens de tri : asc ou desc", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        return duplicataService.rechercherPagee(q, page, size, sort, direction);
    }
}
