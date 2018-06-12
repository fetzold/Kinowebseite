/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickstart.Repositorys;

import org.springframework.data.repository.Repository;

import kickstart.SavedClasses.Comment;
/**
 *
 * @author felix
 */
public interface DBComment extends Repository <Comment, Long> {
  	public Comment save(Comment comment);
	public Iterable<Comment> findAll();
}
