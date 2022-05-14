package com.example.note.Note.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.note.Note.models.Responses.NoteResponseItem
import com.example.note.R
import com.example.note.base.MyApplication
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(var data: List<NoteResponseItem>?) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {


    var openOnClick: ((NoteResponseItem, Int) -> Unit)? = null
    private var textSize = 0
    lateinit var markwon: Markwon
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        setMark()
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        if (data == null) return 0
        else return data!!.size
    }

    fun changeData(list: List<NoteResponseItem>) {
        this.data = list
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: NoteResponseItem? = data?.get(position)

        markwon.setMarkdown(holder.desc, data?.description!!)
        holder.title.text = data?.noteTitle
        holder.date.text = convertTime(data.date!!).toString()
        holder.desc.setOnClickListener { openOnClick?.invoke(data!!, position) }
        holder.itemView.setOnClickListener { openOnClick?.invoke(data!!, position) }

        if (data.isOnline==true){
            holder.state.setImageResource(R.drawable.white)
        }else{
            holder.state.setImageResource(R.drawable.red)
        }


        holder.caed.setBackgroundColor(randomColor())



    }

    private fun randomColor(): Int {
        val androidColors: IntArray = MyApplication.appContext.resources.getIntArray(R.array.androidcolors)
       return androidColors[Random().nextInt(androidColors.size)]
    }

    fun convertTime(time: Long): String? {

        val date = Date(time)
                                        // hh small if you want it 12 hours -- HH if 24 hours
        val format = SimpleDateFormat("dd MMM yyyy hh:mm a")

        return format.format(date)
    }



    fun setTextSizes(textSize: Int) {
        if (textSize != 0) {
            this.textSize = textSize
            notifyDataSetChanged()
        }
    }

    fun setMark() {
        markwon = Markwon.builder(MyApplication.appContext)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(
                TaskListPlugin.create
                    (
                    ResourcesCompat.getColor(
                        MyApplication.appContext.resources,
                        R.color.primary, MyApplication.appContext.theme
                    ),
                    ResourcesCompat.getColor(
                        MyApplication.appContext.resources,
                        R.color.primary, MyApplication.appContext.theme
                    ),
                    ResourcesCompat.getColor(
                        MyApplication.appContext.resources,
                        R.color.background, MyApplication.appContext.theme
                    )
                )
            )
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, _ -> visitor.forceNewLine() }
                }
            })
            .build()
    }

//    fun changeFont(holder: ViewHolder) {
//        val tf_regular = Typeface.createFromAsset(MyApplication.getAppContext().assets, "fonts/a-massir-ballpoint.ttf")
//        holder.daily.typeface = tf_regular
//    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val caed: LinearLayout = item.findViewById(R.id.linear)

        val desc: TextView = item.findViewById(R.id.note_body)
        val title: TextView = item.findViewById(R.id.note_title)
        val date: TextView = item.findViewById(R.id.note_date)
        val state: ImageView = item.findViewById(R.id.state)

//        val date: TextView = item.findViewById(R.id.note_title)


    }


}


