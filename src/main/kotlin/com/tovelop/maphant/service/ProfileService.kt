package com.tovelop.maphant.service

import com.tovelop.maphant.dto.BoardResDto
import com.tovelop.maphant.dto.CommentExtDTO
import com.tovelop.maphant.dto.ProfileNicknameAndBodyAndImageDto
import com.tovelop.maphant.mapper.CommentMapper
import com.tovelop.maphant.mapper.ProfileMapper
import com.tovelop.maphant.type.paging.Pagination
import com.tovelop.maphant.type.paging.PagingDto
import com.tovelop.maphant.type.paging.PagingResponse
import com.tovelop.maphant.utils.UploadUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ProfileService(
    private val uploadUtils: UploadUtils,
    private var profileMapper: ProfileMapper,
    private val uploadLogService: UploadLogService,
    private val commentMapper: CommentMapper
) {
    val defaultProfileImg = "https://upload.wikimedia.org/wikipedia/commons/2/2c/Default_pfp.svg"

    //프로필 이미지의 Dto 불러오기
    fun getNicknameAndBodyAndImage(targetUserId: Int): ProfileNicknameAndBodyAndImageDto =
        profileMapper.findNicknameAndBodyAndImageById(targetUserId)

    //유저가 작성한 댓글 목록 불러오기
    fun getCommentList(userId: Int, targetUserId: Int, params: PagingDto): PagingResponse<CommentExtDTO> {
        var count = profileMapper.cntComment(targetUserId)
        if (userId != targetUserId) {
            count -= profileMapper.cntAnonymousComment(targetUserId)
        }
        if (count < 1) {
            return PagingResponse(Collections.emptyList(), null)
        }
        val pagination = Pagination(count, params)
        val comments = commentMapper.findAllCommentByUser(userId, targetUserId, params)
        return PagingResponse(comments, pagination)
    }

    //유저가 작성한 글 목록 불러오기
    fun getBoardsList(userId: Int, targetUserId: Int, params: PagingDto): PagingResponse<BoardResDto> {
//        getBoardCount xml 구현
        var count = profileMapper.cntBoard(targetUserId)
        if (userId != targetUserId) {
            count -= profileMapper.cntAnonymousAndHideBoard(targetUserId)
        }

        if (count < 1) {
            return PagingResponse(Collections.emptyList(), null)
        }

        //findAllBoardByIdwithPaging xml 구현
        val pagination = Pagination(count, params)
        val boards = profileMapper.findAllBoardByIdWithPaging(userId, targetUserId, params)

        return PagingResponse(boards, pagination)
    }

    fun updateProfileNickname(userId: Int, nickname: String) =
        profileMapper.updateProfileNickname(userId, nickname)

    fun updateProfileBody(userId: Int, body: String){
        existProfile(userId).let {
            if(it)profileMapper.updateProfileBody(userId, body)
            else profileMapper.insertProfileBody(userId, body)
        }

    }

    @Transactional
    fun updateProfileImage(userId: Int, imageUrl: String, file: MultipartFile): String {
        if (uploadUtils.isNotImageFile(file.originalFilename as String))
            throw IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.")

        //uploadLog 남기기
        uploadLogService.storeUrl(userId, imageUrl, file)

//        //현재 userId에 해당하는 profile이 있는지 확인
//        val profile = profileMapper.findById(userId);
//
//        //profile이 현재 없다면 insert 아니면 update
//        if (profile == null) profileMapper.insertProfile(userId, imageUrl)
//        else profileMapper.updateProfileImage(userId, imageUrl)
        existProfile(userId).let {
            if (it) profileMapper.updateProfileImage(userId, imageUrl)
            else profileMapper.insertProfile(userId, imageUrl)
        }

        return imageUrl
    }

    fun updateProfileImageDefault(userId: Int) =
        existProfile(userId).let {
            if (it) profileMapper.updateProfileImage(userId, defaultProfileImg)
            else profileMapper.insertProfile(userId, defaultProfileImg)
        }

    fun existProfile(userId: Int): Boolean = profileMapper.findById(userId) != null

    fun getLikeBoardsList(userId: Int, params: PagingDto): PagingResponse<BoardResDto> {
        var count = profileMapper.findLikeBoardCntByUser(userId)

        if (count < 1) {
            return PagingResponse(Collections.emptyList(), null)
        }

        val pagination = Pagination(count, params)
        val boards = profileMapper.findLikeBoardWithPaging(userId, params)

        return PagingResponse(boards, pagination)
    }
}