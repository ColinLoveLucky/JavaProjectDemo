package com.quark.cobra.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Id;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;

@Data
@Builder
public class PendingUser {
    @Id
    private String id;
    private String data;
    private String conflictingUserId;
    private String conflictNotes;
    private String settleNotes;
    private Boolean resolved = false;
    private Date createDate;
    private String modifiedman;
    private Date modifiedDate;
}
