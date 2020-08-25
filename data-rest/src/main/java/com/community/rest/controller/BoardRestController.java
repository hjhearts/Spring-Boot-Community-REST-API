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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
