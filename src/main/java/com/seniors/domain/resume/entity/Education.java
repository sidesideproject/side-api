package com.seniors.domain.resume.entity;

import com.seniors.domain.common.BaseEntity;
import com.seniors.domain.common.BaseTimeEntity;
import com.seniors.domain.resume.dto.CertificateDto;
import com.seniors.domain.resume.dto.EducationDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Year;

import static java.lang.Boolean.FALSE;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "isDeleted = false")
@SQLDelete(sql = "UPDATE education SET isDeleted = true WHERE id = ?")
public class Education extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci not null COMMENT '기관'")
    private String institution;

    @Column(columnDefinition = "varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci not null COMMENT '전공/과정'")
    private String process;

    @Column(columnDefinition = "int not null COMMENT '시작 연도'")
    private int startedAt;

    @Column(columnDefinition = "int not null COMMENT '종료 연도'")
    private int endedAt;

    @Column(columnDefinition = "text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci not null COMMENT '내용'")
    private String content;

    @Column
    private Boolean isProcessed;

    @Column
    private Boolean isDeleted = FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resumeId")
    private Resume resume;

    @Builder
    public Education(String institution, String process, int startedAt, int endedAt, String content, Boolean isProcessed, Resume resume) {
        this.institution = institution;
        this.process = process;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.content = content;
        this.isProcessed = isProcessed;
        this.resume = resume;
    }

    public static Education from(EducationDto.saveEducationReq saveEducationReq){
        return Education.builder()
                .institution(saveEducationReq.getInstitution())
                .process(saveEducationReq.getProcess())
                .startedAt(saveEducationReq.getStartedAt())
                .endedAt(saveEducationReq.getEndedAt())
                .content(saveEducationReq.getContent())
                .isProcessed(saveEducationReq.getIsProcessed())
                .build();
    }

}
