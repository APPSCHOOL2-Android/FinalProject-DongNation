package likelion.project.dongnation.ui.board

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.model.TipsRipple
import likelion.project.dongnation.repository.BoardRepository

class BoardViewModel : ViewModel() {

    val boardRepository = BoardRepository()
    val boardLiveData = MutableLiveData<MutableList<Tips>>()
    val searchBoardLiveData = MutableLiveData<MutableList<Tips>>()
    val ripplesLiveData = MutableLiveData<MutableList<TipsRipple>>()
    val boardRippleLiveData = MutableLiveData<MutableList<Tips>>()

    fun loadBoard() {
        viewModelScope.launch {
            val board = boardRepository.getAllBoard()
            boardLiveData.postValue(board)
        }
    }

    fun searchBoard(word : String) {
        viewModelScope.launch {
            val search = boardRepository.getAllBoard()
            val searchList = mutableListOf<Tips>()

            for (s in search){
                if (s.tipTitle.contains(word) ||
                    s.tipContent.contains(word)) {

                    searchList.add(s)
                }
            }

            searchBoardLiveData.postValue(searchList)
        }
    }

    fun deleteBoard(board: Tips) {
        viewModelScope.launch {
            boardRepository.deleteBoard(board)
        }
    }

    fun loadRipples(tipIdx: String) {
        viewModelScope.launch {
            val ripples = boardRepository.getRipplesForBoard(tipIdx)
            ripplesLiveData.postValue(ripples)
        }
    }

    fun deleteRipples(tipIdx: String, rippleIdx: String) {
        viewModelScope.launch {
            boardRepository.deleteRipples(tipIdx, rippleIdx)
            // 댓글 삭제 후 ripplesLiveData 업데이트
            val updatedRippleList = ripplesLiveData.value?.toMutableList() ?: mutableListOf()
            updatedRippleList.removeAll { it.rippleIdx == rippleIdx }
            ripplesLiveData.postValue(updatedRippleList)
        }
    }

    fun loadMyBoard(userId : String) {
        viewModelScope.launch {
            val board = boardRepository.getMyBoard(userId)
            boardLiveData.postValue(board)
        }
    }

    fun loadMyRipple(rippleWriterId: String) {
        viewModelScope.launch {
            val ripple = boardRepository.getMyRipple(rippleWriterId)
            boardRippleLiveData.postValue(ripple)
        }
    }

}