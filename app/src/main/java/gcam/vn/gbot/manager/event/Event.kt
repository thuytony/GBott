package gcam.vn.gbot.manager.event

import org.greenrobot.eventbus.EventBus

/**
 * Created by thuythu on 12/01/2018.
 */
class Event{
    companion object {
        fun postEvent(eventMessage: EventMessage){
            EventBus.getDefault().post(eventMessage)
        }
    }
}