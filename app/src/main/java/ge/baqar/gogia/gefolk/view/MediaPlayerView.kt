package ge.baqar.gogia.gefolk.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.SeekBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.databinding.ViewMediaPlayerBinding
import ge.baqar.gogia.gefolk.media.FolkMediaService
import ge.baqar.gogia.gefolk.model.AutoPlayState


class MediaPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    var state: Int = HALF_OPENED
    private lateinit var bottomNavigationView: BottomNavigationView

    private var seeking: Boolean = false
    var onAutoPlayChanged: (() -> Unit)? = null
    var onSizeChange: ((Boolean) -> Unit)? = null
    var onNext: (() -> Unit)? = null
    var onPrev: (() -> Unit)? = null
    var onStop: (() -> Unit)? = null
    var onPlayPause: (() -> Unit)? = null
    var onShare: (() -> Unit)? = null
    var onRewind: ((Int) -> Unit)? = null
    var onFav: (() -> Unit)? = null
    var openPlayListListener: (() -> Unit)? = null
    private var onClose: (() -> Unit)? = null

    private var calculatedHeight = 0
    private var animationDuration = 350L
    private var translate: Float = 0F
    private var minimized = true
    private var measured = false

    private val binding: ViewMediaPlayerBinding by lazy {
        ViewMediaPlayerBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    private val actionButtons by lazy {
        arrayOf(
            binding.collapsedMediaPlayerView.playPauseButton,
            binding.collapsedMediaPlayerView.playerAutoPlayButton,
            binding.collapsedMediaPlayerView.favBtn,
            binding.collapsedMediaPlayerView.shareBtn,
            binding.expandedMediaPlayerView.playPauseButton,
            binding.expandedMediaPlayerView.favBtn,
            binding.expandedMediaPlayerView.playerAutoPlayButton,
            binding.expandedMediaPlayerView.playStopButton,
            binding.expandedMediaPlayerView.playNextButton,
            binding.expandedMediaPlayerView.playPrevButton,
            binding.expandedMediaPlayerView.playerPlaylistButton,
            binding.expandedMediaPlayerView.shareBtn
        )
    }

    init {
        disableButtons()
        translate = context.resources.getDimension(R.dimen.bottom_navigation_heght)
        initMinimizedMediaPlayerListeners()
        initMaximizedMediaPlayerListeners()
    }

    private fun disableButtons() {
        actionButtons.forEach {
            it.isEnabled = false
        }
    }

    private fun enableButtons() {
        actionButtons.forEach {
            it.isEnabled = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!measured) {
            calculatedHeight = MeasureSpec.getSize(heightMeasureSpec)
            binding.expandedMediaPlayerViewContainer.translationY = calculatedHeight.toFloat()
            measured = true
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.collapsedMediaPlayerViewContainer.setOnClickListener(l)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMaximizedMediaPlayerListeners() {
        binding.expandedMediaPlayerView.playerViewCloseBtn.setOnClickListener {
            minimize()
        }

        binding.expandedMediaPlayerView.playerPlaylistButton.setOnClickListener {
            openPlayListListener?.invoke()
        }

        binding.expandedMediaPlayerView.playStopButton.setOnClickListener {
            onStop?.invoke()
            minimize()
        }

        binding.expandedMediaPlayerView.playNextButton.setOnClickListener {
            onNext?.invoke()
        }

        binding.expandedMediaPlayerView.playPrevButton.setOnClickListener {
            onPrev?.invoke()
        }

        binding.expandedMediaPlayerView.playPauseButton.setOnClickListener {
            onPlayPause?.invoke()
        }

        binding.expandedMediaPlayerView.favBtn.setOnClickListener {
            onFav?.invoke()
        }

        binding.expandedMediaPlayerView.playerAutoPlayButton.setOnClickListener {
            onAutoPlayChanged?.invoke()
        }


        binding.expandedMediaPlayerView.playerProgressBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (seeking)
                    onRewind?.invoke(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                seeking = true
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                seeking = false
            }

        })

        var previousY = 0f
        binding.expandedMediaPlayerViewContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    binding.expandedMediaPlayerViewContainer.translationY += event.rawY - previousY
                    previousY = event.rawY
                }

                MotionEvent.ACTION_DOWN -> {
                    previousY = event.rawY
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    val translationY = binding.expandedMediaPlayerViewContainer.translationY
                    if (translationY < calculatedHeight / 1.4) {
                        minimize()
                    } else {
                        maximize()
                    }
                }
            }
            false
        }

        binding.expandedMediaPlayerView.shareBtn.setOnClickListener {
            onShare?.invoke()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "NewApi")
    private fun initMinimizedMediaPlayerListeners() {
        binding.collapsedMediaPlayerView.playerViewCloseBtn.setOnClickListener {
            if (!FolkMediaService.isRunning)
                return@setOnClickListener

            onClose?.invoke()
            maximize()
        }

        binding.collapsedMediaPlayerView.favBtn.setOnClickListener {
            onFav?.invoke()
        }

        binding.collapsedMediaPlayerView.playerAutoPlayButton.setOnClickListener {
            onAutoPlayChanged?.invoke()
        }

        binding.collapsedMediaPlayerView.playPauseButton.setOnClickListener {
            onPlayPause?.invoke()
        }
        var initialY = 0f
        binding.collapsedMediaPlayerViewContainer.setOnTouchListener { _, event ->
            if (!FolkMediaService.isRunning)
                return@setOnTouchListener false

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val calculatedMovingY = calculatedHeight + (event.rawY - initialY)
                    binding.expandedMediaPlayerViewContainer.translationY = calculatedMovingY
                }

                MotionEvent.ACTION_DOWN -> {
                    initialY = event.rawY
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    val translationY = binding.expandedMediaPlayerViewContainer.translationY
                    if (translationY < calculatedHeight / 1.2) {
                        maximize()
                    } else {
                        minimize()
                    }
                }
            }
            false
        }
        binding.collapsedMediaPlayerView.shareBtn.setOnClickListener {
            onShare?.invoke()
        }
    }

    fun setTrackTitle(title: String?, artistName: String?) {
        binding.collapsedMediaPlayerView.playingTrackTitle.text = title
        binding.expandedMediaPlayerView.playingTrackTitle.text = title
        binding.expandedMediaPlayerView.playingTrackArtist.text = artistName
    }

    fun setIsFav(isFav: Boolean) {
        if (isFav) {
            binding.expandedMediaPlayerView.favBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
            binding.collapsedMediaPlayerView.favBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.expandedMediaPlayerView.favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            binding.collapsedMediaPlayerView.favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    fun setAutoPlayState(autoPlayState: Int) {
        when (autoPlayState) {
            AutoPlayState.OFF -> {
                binding.collapsedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_24_off)
                binding.expandedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_24_off)
            }

            AutoPlayState.REPEAT_ALBUM -> {
                binding.collapsedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_24_on)
                binding.expandedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_24_on)
            }

            AutoPlayState.REPEAT_ONE -> {
                binding.collapsedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_one_24)
                binding.expandedMediaPlayerView.playerAutoPlayButton.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            }
        }
    }

    fun setDuration(durationString: String?, duration: Int) {
        binding.expandedMediaPlayerView.playingTrackDurationTime.text = durationString
        binding.expandedMediaPlayerView.playerProgressBar.max = duration
        binding.expandedMediaPlayerView.playingTrackTime.text = durationString
    }

    fun setProgress(time: String?, progress: Int) {
        binding.expandedMediaPlayerView.playingTrackTime.text = time
        binding.expandedMediaPlayerView.playerProgressBar.progress = progress
    }

    fun isPlaying(it: Boolean) {
        if (it) {
            binding.expandedMediaPlayerView.playPauseButton.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
            binding.collapsedMediaPlayerView.playPauseButton.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        } else {
            binding.collapsedMediaPlayerView.playPauseButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
            binding.expandedMediaPlayerView.playPauseButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
        }
    }

    fun maximize() {
        minimized = false
        binding.expandedMediaPlayerViewContainer.animate()
            .setDuration(animationDuration)
            .translationY(0F)
            .start()

        binding.collapsedMediaPlayerViewContainer.animate()
            .setDuration(animationDuration)
            .alpha(0f)
            .start()

        bottomNavigationView.animate()
            .setDuration(animationDuration)
            .translationY(translate)
            .start()
        state = OPENED
        onSizeChange?.invoke(true)
    }

    fun forceMinimize() {
        minimized = true

        binding.expandedMediaPlayerViewContainer.animate()
            .setDuration(1)
            .translationY(measuredHeight.toFloat())
            .start()

        binding.collapsedMediaPlayerViewContainer.animate()
            .setDuration(1)
            .alpha(1f)
            .start()

        bottomNavigationView.animate()
            .setDuration(1)
            .translationY(0F)
            .start()
        state = HALF_OPENED
        onSizeChange?.invoke(false)
    }

    fun minimize() {
        minimized = true

        binding.expandedMediaPlayerViewContainer.animate()
            .setDuration(animationDuration)
            .translationY(measuredHeight.toFloat())
            .start()

        binding.collapsedMediaPlayerViewContainer.animate()
            .setDuration(animationDuration)
            .alpha(1f)
            .start()

        bottomNavigationView.animate()
            .setDuration(animationDuration)
            .translationY(0F)
            .start()
        state = HALF_OPENED
        onSizeChange?.invoke(false)
    }

    fun show() {
        enableButtons()

        if (minimized) {
            minimize()
        } else {
            maximize()
        }

        state = HALF_OPENED
    }

    fun setupWithBottomNavigation(navView: BottomNavigationView) {
        bottomNavigationView = navView
    }

    companion object {
        const val OPENED = 1
        const val CLOSED = 2
        const val HALF_OPENED = 3
    }
}