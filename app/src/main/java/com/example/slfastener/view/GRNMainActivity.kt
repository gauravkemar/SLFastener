package com.example.slfastener.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.GRNMainCompletedAdapter
import com.example.slfastener.adapter.GrnMainAdapter
import com.example.slfastener.adapter.GrnMainAddAdapter
import com.example.slfastener.databinding.ActivityGrnmainBinding
import com.example.slfastener.model.grnmain.GetFilteredGRNRequest
import com.example.slfastener.model.grnmain.GetFilteredGRNResponse
import com.example.slfastener.viewmodel.GRNTransactionViewModel
import com.example.slfastener.viewmodel.GRNTransactionViewModelProviderFactory
import es.dmoral.toasty.Toasty
import java.util.ArrayList

class GRNMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrnmainBinding
    private lateinit var session: SessionManager
    private var grnMainItemAdapter: GrnMainAdapter? = null
    private var grnMainCompletedAdapter: GRNMainCompletedAdapter? = null
    lateinit var grnMainResponse: ArrayList<GetFilteredGRNResponse>
    lateinit var grnMainResponseCompleted: ArrayList<GetFilteredGRNResponse>
    private lateinit var userDetails: HashMap<String, Any?>
    private lateinit var viewModel: GRNTransactionViewModel
    var token:String=""
    private lateinit var progress: ProgressDialog
    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grnmain)
        session = SessionManager(this)
        grnMainResponse = ArrayList()
        grnMainResponseCompleted = ArrayList()
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"
        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = GRNTransactionViewModelProviderFactory(application, slFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[GRNTransactionViewModel ::class.java]
        getGrnList("Draft")
        getFilteredGRNCompleted("Submitted")
        binding.rcGrnMainCompleted.visibility=View.GONE
        binding.logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
        binding.mcvAddGrn.setOnClickListener {
            var intent = Intent(this@GRNMainActivity, GRNAddActivity::class.java)
            startActivity(intent)
        }
        binding.mcvGRNDraft.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)
        binding.tvDraft.setTextColor(resources.getColor(R.color.black))
        binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
        binding.mcvCancel.setOnClickListener {
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

     /*   binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
        binding.tvDraft.setTextColor(resources.getColor(R.color.white))
        binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
        binding.mcvCancel.setOnClickListener {
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }*/
        binding.mcvGRNDraft.setOnClickListener {
            //binding.clCompleted.setBackgroundResource(R.drawable.new_ui_unselect_draft_complete_bg)
            //binding.clDraft.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)

            binding.mcvGRNDraft.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)
             binding.mcvGRNCompleted.setBackgroundResource(R.drawable.back_ground_white)

            binding.tvDraft.setTextColor(resources.getColor(R.color.blue))
            binding.tvCompleted.setTextColor(resources.getColor(R.color.black))

            //binding.clCompleted.setBackgroundResource(R.drawable.new_ui_unselect_draft_complete_bg)
            //binding.clDraft.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)

            getGrnList("Draft")
            binding.rcGrnMain.visibility=View.VISIBLE
            binding.rcGrnMainCompleted.visibility=View.GONE
        }
        binding.mcvGRNCompleted.setOnClickListener {
            binding.mcvGRNDraft.setBackgroundResource(R.drawable.back_ground_white)
            binding.mcvGRNCompleted.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)

            binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
            binding.tvDraft.setTextColor(resources.getColor(R.color.black))

            //binding.clDraft.setBackgroundResource(R.drawable.new_ui_unselect_draft_complete_bg)
           // binding.clCompleted.setBackgroundResource(R.drawable.new_ui_select_draft_complete_bg)

            //getGrnList("Complete")
            getFilteredGRNCompleted("Submitted")
            binding.rcGrnMainCompleted.visibility=View.VISIBLE
            binding.rcGrnMain.visibility=View.GONE
        }

        viewModel.getFilteredGRNMutableResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    grnMainResponse.clear()
                    response.data?.let { resultResponse ->
                        try {
                            if(resultResponse.size>0)
                            {
                                grnMainResponse.addAll(resultResponse)
                                setGrnList(grnMainResponse)
                                binding.tvDraftCount.setText(grnMainResponse.size.toString())
                            }
                            else
                            {
                                //Toast.makeText(this,"List is Empty!!",Toast.LENGTH_SHORT).show()
                                setGrnList(grnMainResponse)
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNMainActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNMainActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNMainActivity)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getFilteredGRNCompletedMutableResponse .observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    grnMainResponseCompleted.clear()
                    response.data?.let { resultResponse ->
                        try {
                            if(resultResponse.size>0)
                            {
                                grnMainResponseCompleted.addAll(resultResponse)
                                setGrnCompletedList(grnMainResponseCompleted)
                                binding.tvCompletedCount.setText(grnMainResponseCompleted.size.toString())
                            }
                            else
                            {
                                //Toast.makeText(this,"List is Empty!!",Toast.LENGTH_SHORT).show()
                                setGrnCompletedList(grnMainResponseCompleted)
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRNMainActivity,
                                e.printStackTrace().toString(),
                                Toasty.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { errorMessage ->
                        Toasty.error(
                            this@GRNMainActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRNMainActivity)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getGrnList("Draft")
        getFilteredGRNCompleted("Submitted")
    }

    private fun showProgressBar() {
        progress.show()
    }

    private fun hideProgressBar() {
        progress.cancel()
    }
    private fun getGrnList(status: String)
    {
        try {
            viewModel.getFilteredGRN(this,token,baseUrl, GetFilteredGRNRequest(status))
        }
        catch (e:Exception)
        {
            Toasty.warning(
                this@GRNMainActivity,
                "Please Select Po from list!!",
                Toasty.LENGTH_SHORT
            ).show()
        }
    }
    private fun getFilteredGRNCompleted(status: String)
    {
        try {
            viewModel.getFilteredGRNCompleted(this,token,baseUrl, GetFilteredGRNRequest(status))
        }
        catch (e:Exception)
        {
            Toasty.warning(
                this@GRNMainActivity,
                "Please Select Po from list!!",
                Toasty.LENGTH_SHORT
            ).show()
        }
    }

    private fun setGrnList(grnMainResponse: ArrayList<GetFilteredGRNResponse>) {
        grnMainItemAdapter = GrnMainAdapter{grnId->
            var intent=Intent(this@GRNMainActivity,GRNAddActivity::class.java)
            intent.putExtra("GRNID",grnId)
            startActivity(intent)
        }
        grnMainItemAdapter?.setGrnMainList(grnMainResponse, this@GRNMainActivity)
        binding.rcGrnMain!!.adapter = grnMainItemAdapter
        binding.rcGrnMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private fun setGrnCompletedList(grnMainResponse: ArrayList<GetFilteredGRNResponse>) {
        grnMainCompletedAdapter = GRNMainCompletedAdapter{grnId->
            var intent=Intent(this@GRNMainActivity,CompletedGRNActivity::class.java)
            intent.putExtra("GRNID",grnId)
            startActivity(intent)
        }
        grnMainCompletedAdapter?.setGrnMainList(grnMainResponse, this@GRNMainActivity)
        binding.rcGrnMainCompleted!!.adapter = grnMainCompletedAdapter
        binding.rcGrnMainCompleted.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel") { dialog, which ->

                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }
    private fun logout(){
        session.logoutUser()
        startActivity(Intent(this@GRNMainActivity, LoginActivity::class.java))
        finish()
    }
}



/*  binding.mcvAddGrn.setOnClickListener {
            var intent = Intent(this@GRNMainActivity, GRNAddActivity::class.java)
            startActivity(intent)
        }


        binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
        binding.tvDraft.setTextColor(resources.getColor(R.color.white))
        binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
        filter("Draft")

        binding.mcvCancel.setOnClickListener {
            var intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }



        binding.mcvGRNDraft.setOnClickListener {

            binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
            binding.tvDraft.setTextColor(resources.getColor(R.color.white))
            binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
            binding.mcvGRNCompleted.setCardBackgroundColor(resources.getColor(R.color.white))
            filter("Draft")
        }
        binding.mcvGRNCompleted.setOnClickListener {

            binding.tvCompleted.setTextColor(resources.getColor(R.color.white))
            binding.tvDraft.setTextColor(resources.getColor(R.color.blue))
            binding.mcvGRNCompleted.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
            binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.white))
            filter("Completed")
        }

        val filteredDraftList =
            ArrayList(grnMainResponse.filter {
                it.Status.lowercase().contains("Draft".lowercase())
            })

        val filteredCompletedList =
            ArrayList(grnMainResponse.filter {
                it.Status.lowercase().contains("Completed".lowercase())
            })
         binding.tvCompletedCount.setText(filteredCompletedList.size.toString())
         binding.tvDraftCount.setText(filteredDraftList.size.toString())*/

/*private fun setGrnList(grnMainResponse: ArrayList<GrnMainListResponse>) {
    grnMainItemAdapter = GrnMainAdapter()
    grnMainItemAdapter?.setGrnMainList(grnMainResponse, this@GRNMainActivity)
    binding.rcGrnMain!!.adapter = grnMainItemAdapter
    binding.rcGrnMain.layoutManager =
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


}*/



/*   private  fun filter(string: String){
        val filteredParkingList =
            ArrayList(grnMainResponse.filter {
                it.Status.lowercase().contains(string.lowercase())
            })

        setGrnList(filteredParkingList)
    }*/

/*  grnMainResponse.addAll(
            listOf(
                GrnMainListResponse(
                    "123 - XYZ Supplier",
                    "12:00:23",
                    "GRN123113",
                    "1",
                    "Draft",
                    "Import"
                ),
                GrnMainListResponse(
                    "845 - FGH Supplier",
                    "12:00:23",
                    "GRN123113",
                    "2",
                    "Draft",
                    "Import"
                ),
                GrnMainListResponse(
                    "789 - DEF Supplier",
                    "13:15:00",
                    "GRN789012",
                    "3",
                    "Completed",
                    "Import"
                ),
                GrnMainListResponse(
                    "965 - GHJ Supplier",
                    "12:30:45",
                    "GRN485465",
                    "4",
                    "Completed",
                    "Export"
                ),
                GrnMainListResponse(
                    "456 - OJK Supplier",
                    "12:30:45",
                    "GRN457456",
                    "4",
                    "Completed",
                    "Export"
                ),
                GrnMainListResponse(
                    "745 - PLM Supplier",
                    "12:30:45",
                    "GRN459632",
                    "4",
                    "Completed",
                    "Export"
                ),
                GrnMainListResponse(
                    "235 - ASD Supplier",
                    "12:30:45",
                    "GRN458547",
                    "4",
                    "Draft",
                    "Export"
                ),
                GrnMainListResponse(
                    "658 - UYR Supplier",
                    "12:30:45",
                    "GRN459874",
                    "4",
                    "Draft",
                    "Export"
                )
            )
        )*/