package com.community.rest.event;

import com.community.rest.domain.Board;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;


@RepositoryEventHandler
@Component
public class BoardEventHandler {

    /**
     * @param board body for post method
     */
    @HandleAfterCreate(value = Board.class)
    public void beforeCreateBoard(Board board){
        System.out.println("HandleBeforeCrate Activated");
        board.setCreatedDateNow();
    }

    /**
     * @param board body for put method
     */
    @HandleBeforeSave
    public void beforeSaveBoard(Board board){
        System.out.println("HandleBeforeSave Activated");
        board.setUpdatedDateNow();
    }
}
