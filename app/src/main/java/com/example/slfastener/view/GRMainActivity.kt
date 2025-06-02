package com.example.slfastener.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demorfidapp.helper.Constants
import com.example.demorfidapp.helper.Resource
import com.example.demorfidapp.helper.SessionManager
import com.example.demorfidapp.repository.SLFastenerRepository
import com.example.slfastener.R
import com.example.slfastener.adapter.gradapters.GRMainAdapter
import com.example.slfastener.adapter.gradapters.GRMainCompletedAdapter
import com.example.slfastener.databinding.ActivityGrmainBinding
import com.example.slfastener.model.goodsreceipt.GetAllGRResponse
import com.example.slfastener.viewmodel.GRViewModel
import com.example.slfastener.viewmodel.GRViewModelFactory
import es.dmoral.toasty.Toasty
import java.util.ArrayList

class GRMainActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrmainBinding
    private lateinit var session: SessionManager
    private var grnMainItemAdapter: GRMainAdapter? = null
    private var grnMainCompletedAdapter: GRMainCompletedAdapter? = null
    lateinit var grnMainResponse: ArrayList<GetAllGRResponse>
    lateinit var grnMainResponseCompleted: ArrayList<GetAllGRResponse>
    private lateinit var userDetails: HashMap<String, Any?>
    private lateinit var viewModel: GRViewModel
    var token:String=""
    private var baseUrl: String =""
    private var serverIpSharedPrefText: String? = null
    private var serverHttpPrefText: String? = null
    private lateinit var progress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_grmain)
        session = SessionManager(this)
        grnMainResponse = ArrayList()
        grnMainResponseCompleted = ArrayList()
        grnMainResponseCompleted = ArrayList()
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")
        userDetails = session.getUserDetails()
        token = userDetails["jwtToken"].toString()
        serverIpSharedPrefText = userDetails!![Constants.KEY_SERVER_IP].toString()
        serverHttpPrefText = userDetails!![Constants.KEY_HTTP].toString()
        baseUrl = "$serverHttpPrefText://$serverIpSharedPrefText/service/api/"

        val slFastenerRepository = SLFastenerRepository()
        val viewModelProviderFactory = GRViewModelFactory(application, slFastenerRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[GRViewModel ::class.java]
        getGrnList("Draft")
        getFilteredGRNCompleted("Submitted")
        binding.rcGrnMainCompleted.visibility= View.GONE
        binding.logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
        binding.mcvAddGrn.setOnClickListener {
            var intent = Intent(this@GRMainActivity, GoodsReceiptActivity::class.java)
            startActivity(intent)
        }
        binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
        binding.tvDraft.setTextColor(resources.getColor(R.color.white))
        binding.tvCompleted.setTextColor(resources.getColor(R.color.blue))
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
            getGrnList("Draft")
            binding.rcGrnMain.visibility= View.VISIBLE
            binding.rcGrnMainCompleted.visibility= View.GONE
        }
        binding.mcvGRNCompleted.setOnClickListener {
            binding.tvCompleted.setTextColor(resources.getColor(R.color.white))
            binding.tvDraft.setTextColor(resources.getColor(R.color.blue))
            binding.mcvGRNCompleted.setCardBackgroundColor(resources.getColor(R.color.lighter_blue))
            binding.mcvGRNDraft.setCardBackgroundColor(resources.getColor(R.color.white))
            //getGrnList("Complete")
            getFilteredGRNCompleted("Submitted")
            binding.rcGrnMainCompleted.visibility= View.VISIBLE
            binding.rcGrnMain.visibility= View.GONE
        }

        viewModel.getAllGRResponseMutableList.observe(this) { response ->
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
                                this@GRMainActivity,
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
                            this@GRMainActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRMainActivity)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }
        viewModel.getAllGRCompleteResponse.observe(this) { response ->
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
                                setGrnCompletedList(grnMainResponseCompleted)
                            }

                        } catch (e: Exception) {
                            Toasty.warning(
                                this@GRMainActivity,
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
                            this@GRMainActivity,
                            "Login failed - \nError Message: $errorMessage"
                        ).show()
                        session.showToastAndHandleErrors(errorMessage, this@GRMainActivity)
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
            viewModel.getAllGRResponse(token, baseUrl, status)
        }
        catch (e:Exception)
        {
            Toasty.warning(
                this@GRMainActivity,
                "Please Select Po from list!!",
                Toasty.LENGTH_SHORT
            ).show()
        }
    }
    private fun getFilteredGRNCompleted(status: String)
    {
        try {
            viewModel.getAllGRCompleteResponse(token, baseUrl, status)
        }
        catch (e:Exception)
        {
            Toasty.warning(
                this@GRMainActivity,
                "Please Select Po from list!!",
                Toasty.LENGTH_SHORT
            ).show()
        }
    }

    private fun setGrnList(grnMainResponse: ArrayList<GetAllGRResponse>) {
        grnMainItemAdapter = GRMainAdapter{ grnId->
            var intent= Intent(this@GRMainActivity,GoodsReceiptActivity::class.java)
            intent.putExtra("GRNID",grnId)
            startActivity(intent)
        }
        grnMainItemAdapter?.setGrnMainList(grnMainResponse, this@GRMainActivity)
        binding.rcGrnMain!!.adapter = grnMainItemAdapter
        binding.rcGrnMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private fun setGrnCompletedList(grnMainResponse: ArrayList<GetAllGRResponse>) {
        grnMainCompletedAdapter = GRMainCompletedAdapter{grnId->
            var intent= Intent(this@GRMainActivity,GRSubmittedActivity::class.java)
            intent.putExtra("GRNID",grnId)
            startActivity(intent)
        }
        grnMainCompletedAdapter?.setGrnMainList(grnMainResponse, this@GRMainActivity)
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
        startActivity(Intent(this@GRMainActivity, LoginActivity::class.java))
        finish()
    }
}
