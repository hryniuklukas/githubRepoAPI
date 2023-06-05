package com.atipera.ghapi.controllers;

import com.atipera.ghapi.exceptions.ApiError;
import com.atipera.ghapi.exceptions.UserNotFoundException;
import com.atipera.ghapi.exceptions.WrongHeaderException;
import com.atipera.ghapi.models.RepoDetails;
import com.atipera.ghapi.services.GitHubService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GitHubController {
    private final GitHubService gitHubService;


    public GitHubController(GitHubService gitHubService) {
        this.gitHubService=gitHubService;
    }

    @GetMapping(value = "/repos/{username}")
    public ResponseEntity<Object> getRepos(@RequestHeader(HttpHeaders.ACCEPT) String accept, @PathVariable String username){
        try{
        return gitHubService.serveRequest(username, accept);}
        catch(UserNotFoundException ex){
            ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
            return new ResponseEntity<>(error, error.getStatus());
        }
        catch(WrongHeaderException ex){
            ApiError error = new ApiError(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
            return new ResponseEntity<>(error,error.getStatus());
        }
    }
}
