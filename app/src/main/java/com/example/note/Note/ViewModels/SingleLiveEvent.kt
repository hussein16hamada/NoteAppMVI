package com.example.note.Note.ViewModels

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and [Snackbar] messages.
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setExtraValue() or call().
 *
 * Note that only one observer is going to be notified of changes.
 */
class SingleLiveEvent<T> : LiveData<T>() {

    companion object {
        private val TAG = SingleLiveEvent::class.simpleName
    }

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    public override fun setValue(t: T) {
        pending.set(true)
        super.setValue(t)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = super.getValue() as T
}