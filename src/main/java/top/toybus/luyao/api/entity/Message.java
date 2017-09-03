package top.toybus.luyao.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * 消息
 */
@Data
@Entity
@Table(name = "tb_message")
@SuppressWarnings("serial")
public class Message implements Serializable {
	
    @Id
    @GeneratedValue
    private Long id;

    private Long user_id;
    
    private String user_name;
    
    private Long mark_id;
    
    private String mark;

    private String message;
   
    private LocalDateTime create_time;

}
