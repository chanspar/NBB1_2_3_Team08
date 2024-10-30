package edu.example.learner_kotlin.member.controller

import edu.example.learner_kotlin.member.service.MemberService
import edu.example.learner_kotlin.log
import edu.example.learner_kotlin.member.dto.MemberDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/members")
@Tag(name = "회원 컨트롤러", description = "회원 조회, 수정, 탈퇴와 관련된 API입니다.")
class MemberRestController (private val memberService: MemberService){

    @PutMapping("/{memberId}/image")
    @Operation(summary = "이미지 업로드", description = "사진 파일을 받아 프로필 사진을 변경합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "프로필 변경에 성공하였습니다."
        ), ApiResponse(
            responseCode = "404",
            description = "프로필 변경에 실패하였습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(example = "{\"error\": \"프로필 변경에 실패하였습니다.\"}")
            )]
        )]
    )
    fun memberUploadImage(
        @RequestParam("file") file: MultipartFile,
        @PathVariable memberId: Long?
    ): ResponseEntity<String> {
        log.info("--- memberUploadImage()")
        //파일 크기 제한
        if (!file.isEmpty && file.size > 2097152) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body<String>("파일 크기가 너무 큽니다.")
        }

        //이미지 파일인지 확인
        if (file.contentType?.startsWith("image/") == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body<String>("이미지 파일만 업로드 가능해요👻")
        }

        try {
            memberService.uploadImage(file, memberId)
            return ResponseEntity.status(HttpStatus.CREATED).body<String>("Image uploaded successfully")
        } catch (e: Exception) {
            log.error("Error uploading image", e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body<String>("Error uploading image: " + e.message)
        }
    }

    //이미지 삭제
    @DeleteMapping("{memberId}/image")
    @Operation(summary = "이미지 삭제", description = "프로필 사진을 삭제합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "프로필 삭제에 성공하였습니다."
        ), ApiResponse(
            responseCode = "404",
            description = "프로필 삭제에 실패하였습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(example = "{\"error\": \"프로필 삭제에 실패하였습니다.\"}")
            )]
        )]
    )
    fun deleteMember(@PathVariable memberId: Long?): ResponseEntity<String> {
        log.info("--- memberDelete()")
        memberService.removeImage(memberId)

        return ResponseEntity.status(HttpStatus.CREATED).body<String>("이미지가 성공적으로 삭제되었습니다.")
    }

    //마이페이지
    @GetMapping("/{memberId}")
    @Operation(summary = "회원 조회", description = "회원의 개인정보를 가져옵니다")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "회원 조회에 성공하였습니다."
        ), ApiResponse(
            responseCode = "404",
            description = "회원 조회에 실패하였습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(example = "{\"error\": \"회원 조회에 실패하였습니다.\"}")
            )]
        )]
    )
    fun myPageRead(@PathVariable memberId: Long?): ResponseEntity<MemberDTO> {
        log.info("--- myPageRead()")
        log.info(memberId)
        log.info(memberService.getMemberInfo(memberId))

        return ResponseEntity.ok(memberService.getMemberInfo(memberId))
    }

    //다른 사용자 조회
    @GetMapping("/other/{nickname}")
    @Operation(summary = "다른 회원 조회", description = "다른 회원의 공개된 정보를 가져옵니다")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "다른 회원 조회에 성공하였습니다."
        ), ApiResponse(
            responseCode = "404",
            description = "다른 회원 조회에 실패하였습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(example = "{\"error\": \"다른 회원 조회에 실패하였습니다.\"}")
            )]
        )]
    )
    fun memberRead(@PathVariable nickname: String): ResponseEntity<MemberDTO> {
        log.info("--- memberRead()")
        val memberDTO: MemberDTO = memberService.getMemberInfoNickName(nickname)
        //본인이 아닌 사용자 조회시 개인정보빼고 정보 전달
        return ResponseEntity.ok(memberDTO)
    }

    //회원 정보 수정
    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정", description = "회원의 이메일, 비밀번호, 닉네임을 변경합니다.")
    @ApiResponses(
        value = [ApiResponse(
            responseCode = "200",
            description = "회원 정보 수정회에 성공하였습니다."
        ), ApiResponse(
            responseCode = "404",
            description = "회원 정보 수정에 실패하였습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(example = "{\"error\": \"회원 정보 수정에 실패하였습니다.\"}")
            )]
        )]
    )
    fun memberModify(
        @RequestBody @Validated memberDTO: MemberDTO,
        @PathVariable memberId: Long
    ): ResponseEntity<MemberDTO> {
        log.info("--- memberModify()")

        return ResponseEntity.ok(memberService.updateMemberInfo(memberId, memberDTO))
    }

    //비밀번호 인증
    @PostMapping("/{memberId}/verify-password")
    fun verifyPassword(@PathVariable memberId: Long?, @RequestBody password: String): ResponseEntity<String> {
        val isVerified: Boolean = memberService.verifyPassword(memberId, password)
        log.info("password : $password")
        return if (isVerified) {
            ResponseEntity.ok<String>("비밀번호 인증 성공!")
        } else {
            ResponseEntity.status(403).body<String>("비밀번호가 일치하지 않습니다.")
        }
    }

    //회원 탈퇴
    @DeleteMapping("/{memberId}")
    fun memberDelete(@PathVariable memberId: Long?): ResponseEntity<String> {
        log.info("--- memberDelete()")

        memberService.deleteMember(memberId)

        return ResponseEntity.ok<String>("회원 탈퇴에 성공하였습니다.")
    }

    //강사 이름으로 조회
    @GetMapping("/instructor/{nickname}")
    fun getInstructorByNickname(@PathVariable nickname: String?): ResponseEntity<MemberDTO> {
        log.info("--- myPageRead()")
        log.info(nickname)
        return ResponseEntity.ok(memberService.getMemberInfoNickName(nickname))
    }

    //회원 목록 조회
    @GetMapping("/list")
    fun listMembers(): ResponseEntity<List<MemberDTO>> {
        log.info("--- myPageRead()")

        return ResponseEntity.ok(memberService.allMembers())
    }
}
