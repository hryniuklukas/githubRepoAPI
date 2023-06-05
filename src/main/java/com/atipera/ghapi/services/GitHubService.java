package com.atipera.ghapi.services;

import com.atipera.ghapi.exceptions.ApiError;
import com.atipera.ghapi.exceptions.UserNotFoundException;
import com.atipera.ghapi.exceptions.WrongHeaderException;
import com.atipera.ghapi.models.BranchDetails;

import com.atipera.ghapi.models.Repo;
import com.atipera.ghapi.models.RepoDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GitHubService {
  private final RestTemplate restTemplate;

  public GitHubService() {
    this.restTemplate = new RestTemplate();
  }

  private ResponseEntity<Object> buildError(ApiError error) {
    return new ResponseEntity<>(error, error.getStatus());
  }
  public ResponseEntity<Object> serveRequest(String username, String accept) throws WrongHeaderException, UserNotFoundException {
    if(accept.equals("application/xml")){
      throw new WrongHeaderException("Application/xml not accepted");
    }else if(accept.equals("application/json")){
      return getRepos(username);
    }
   return null;
  }
  public ResponseEntity<Object> getRepos(String username) throws UserNotFoundException {

      URI uri =
          UriComponentsBuilder.fromUriString("https://api.github.com")
              .pathSegment("users", username, "repos")
              .queryParam("per_page", 100)
              .build()
              .toUri();
      try {
        RequestEntity<Void> requestEntity =
            RequestEntity.get(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        ResponseEntity<Repo[]> responseEntity = restTemplate.exchange(requestEntity, Repo[].class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
          Repo[] repos = responseEntity.getBody();
          if (repos != null) {
            List<RepoDetails> reposWithBranches = new ArrayList<>();
            for (Repo repo : repos) {
              if (!repo.isFork()) {
                RepoDetails repoDetails = new RepoDetails(repo.getName());
                repoDetails.setBranchDetails(getBranchesForRepo(username, repo.getName()));
                reposWithBranches.add(repoDetails);
              }
            }
            return new ResponseEntity<>(reposWithBranches, HttpStatus.OK);
          }
        }
      } catch (HttpClientErrorException exception) {
        throw new UserNotFoundException("Given GH User not found");
      }

    return buildError(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown GH API error"));
  }

  private List<BranchDetails> getBranchesForRepo(String username, String repoName) {
    URI uri =
        UriComponentsBuilder.fromUriString("https://api.github.com")
            .pathSegment("repos", username, repoName, "branches")
            .queryParam("per_page", 100)
            .build()
            .toUri();

    RequestEntity<Void> requestEntity =
        RequestEntity.get(uri).header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();

    ResponseEntity<BranchDetails[]> responseEntity =
        restTemplate.exchange(requestEntity, BranchDetails[].class);

    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      BranchDetails[] branches = responseEntity.getBody();
      if (branches != null) {
        return Arrays.asList(branches);
      }
    }
    return Collections.emptyList();
  }
}
