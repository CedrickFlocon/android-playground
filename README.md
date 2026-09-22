# android-playground

Android Playground project

## Feedback

First of all, thank you Marvin for the opportunity and for the technical test.

I used my personal playground project, which I regularly use to experiment things. This is why the project contains a few unused libraries that are not relevant to the implementation itself.

I spent a bit more than two hours implementing the two network calls as well as the pager/player interaction. Given the time spent on those parts, I decided not to implement the scrubber in order to be able to write this.

For the sake of speed I did not introduce additional interfaces or split the code into separate modules. In a production codebase, I would normally introduce clearer abstractions and separation of concerns. Another possible approach would be to split the two different API into separate modules.

Potential improvements

Since instantiating ExoPlayer is relatively expensive, we could maintain a pool of players and reuse them. 
I should have use only one Okhttp client, since it's perform better(share tls connection/thread pool).
One area I would improve is the way the video fetch is triggered. Currently, settledPage is used as the trigger for fetching the video. I would decouple these two concerns so that video data can be fetched earlier, allowing the corresponding posters to be displayed while navigating between pages.

With more time, I would also move the page and video states out of the ViewModel and make them persistent in the domain layer rather than relying only on WhileSubscribed of the ViewModel.
I would structure the state/events flow around structure like that :
```
interface ListUseCase {

    val events: MutableSharedFlow<Event>
    val search: Flow<State>

    sealed interface Event {
        object Retry : Event
        data class RetrieveVideo(val id: String) : Event
        object NextPage : Event
    }

    data class State(
        val hasNext: Boolean,
        val error: Boolean,
        val loading: Boolean,
        val items: List<VideoInfo>?,
    )
}
```

This would give the ViewModel a thinner role, keep the state management closer to the business logic, and make the different UI states easier to reason about and test independently.
