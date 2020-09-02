package com.community.rest.controller;

import com.community.rest.domain.Board;
import com.community.rest.repository.BoardRepository;
import com.community.rest.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RepositoryRestController
@RequestMapping("/api/boards")
public class BoardRestController {
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    public BoardRestController(BoardRepository boardRepository, UserRepository userRepository){
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBoards(@PageableDefault Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(pageable.getPageSize(), boards.getNumber(), boards.getTotalElements());
        PagedResources<Board> resources = new PagedResources<>(boards.getContent(), pageMetadata);
        resources.add(linkTo(methodOn(BoardRestController.class).getBoards(pageable)).withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @PostMapping
    public ResponseEntity<?> postBoard(@RequestBody Board board){
        board.setCreatedDateNow();
        boardRepository.save(board);
        return new ResponseEntity<>("{}", HttpStatus.CREATED);
    }

    @PutMapping("/{idx}")
    public ResponseEntity<?> putBoard(@PathVariable("idx") Long idx, @RequestBody Board board){
        Board persistBoard = boardRepository.getOne(idx);
        persistBoard.update(board);
        boardRepository.save(persistBoard);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    @DeleteMapping("/{idx}")
    public ResponseEntity<?> deleteBoard(@PathVariable("idx") Long idx){
        boardRepository.deleteById(idx);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
    @GetMapping("/boards")
    public @ResponseBody Resources<Board> simpleBoard(@PageableDefault Pageable pageable){
        Page<Board> boardList = boardRepository.findAll(pageable);
        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(pageable.getPageSize(), boardList.getNumber(), boardList.getTotalElements());
        PagedResources<Board> pagedResources = new PagedResources<>(boardList.getContent(), pageMetadata);
        pagedResources.add(linkTo(methodOn(BoardRestController.class).simpleBoard(pageable)).withSelfRel());
        return pagedResources;
    }

    @GetMapping("/corona")
    public ResponseEntity<?> getCorona() throws UnsupportedEncodingException,  URISyntaxException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson"); /*URL*/
        RestTemplate template = new RestTemplate();
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLDecoder.decode(URLEncoder.encode("bzHeLyHn1eU7Zd0Vcw3%2BB8UWymKAxuQxo%2Fq5la9XlJvHKOStYuIySV44whfV17WSKlJWWE8EyP5x6vWsRxpEdg%3D%3D", "UTF-8"), "UTF-8"));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        URI uri = new URI(urlBuilder.toString());
        HttpEntity entity = new HttpEntity("parameters", httpHeaders);
        return new ResponseEntity<>(template.getForObject(uri, String.class), HttpStatus.OK);
    }
}
