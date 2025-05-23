package com.example.checkscam.rest;



import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;
import com.example.checkscam.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    public PaginatedResponseDTO<TournamentResponseDTO> getAllTournaments(TournamentRequestDTO request) {
        return tournamentService.getAllTournaments(request);
    }
}