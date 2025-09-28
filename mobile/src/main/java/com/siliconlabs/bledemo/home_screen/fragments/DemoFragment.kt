package com.siliconlabs.bledemo.home_screen.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.siliconlabs.bledemo.R
import com.siliconlabs.bledemo.databinding.FragmentDemoBinding
import com.siliconlabs.bledemo.home_screen.adapters.DemoAdapter
import com.siliconlabs.bledemo.home_screen.base.BaseServiceDependentMainMenuFragment
import com.siliconlabs.bledemo.home_screen.base.BluetoothDependent
import com.siliconlabs.bledemo.home_screen.base.LocationDependent
import com.siliconlabs.bledemo.home_screen.dialogs.SelectDeviceDialog
import com.siliconlabs.bledemo.home_screen.menu_items.DemoMenuItem
import com.siliconlabs.bledemo.home_screen.menu_items.HealthThermometer
import com.siliconlabs.bledemo.utils.BLEUtils

class DemoFragment : BaseServiceDependentMainMenuFragment(), DemoAdapter.OnDemoItemClickListener,
    DialogInterface.OnDismissListener {

    private val binding by viewBinding(FragmentDemoBinding::bind)
    private var demoAdapter: DemoAdapter? = null
    private val list: ArrayList<DemoMenuItem> = ArrayList()
    private var selectDeviceDialog: SelectDeviceDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        list.apply {
            add(
                HealthThermometer(
                    R.drawable.redesign_ic_demo_health_thermometer,
                    getString(R.string.title_Health_Thermometer),
                    getString(R.string.main_menu_description_thermometer)
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_demo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.main_navigation_demo_title)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        demoAdapter = DemoAdapter(list, this@DemoFragment)
        binding.rvDemoMenu.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = demoAdapter
        }
        demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
    }

    override val bluetoothDependent = object : BluetoothDependent {

        override fun onBluetoothStateChanged(isBluetoothOn: Boolean) {
            toggleBluetoothBar(isBluetoothOn, binding.bluetoothBar)
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
            if (!isBluetoothOn) selectDeviceDialog?.dismiss()
        }

        override fun onBluetoothPermissionsStateChanged(arePermissionsGranted: Boolean) {
            toggleBluetoothPermissionsBar(arePermissionsGranted, binding.bluetoothPermissionsBar)
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible())
            if (!arePermissionsGranted) selectDeviceDialog?.dismiss()
        }

        override fun refreshBluetoothDependentUi(isBluetoothOperationPossible: Boolean) {
            demoAdapter?.toggleItemsEnabled(isBluetoothOperationPossible)
        }

        override fun setupBluetoothPermissionsBarButtons() {
            binding.bluetoothPermissionsBar.setFragmentManager(childFragmentManager)
        }
    }

    override val locationDependent = object : LocationDependent {

        override fun onLocationStateChanged(isLocationOn: Boolean) {
            toggleLocationBar(isLocationOn, binding.locationBar)
        }

        override fun onLocationPermissionStateChanged(isPermissionGranted: Boolean) {
            toggleLocationPermissionBar(isPermissionGranted, binding.locationPermissionBar)
            demoAdapter?.toggleItemsEnabled(isPermissionGranted)
            if (!isPermissionGranted) selectDeviceDialog?.dismiss()
        }

        override fun setupLocationBarButtons() {
            binding.locationBar.setFragmentManager(childFragmentManager)
        }

        override fun setupLocationPermissionBarButtons() {
            binding.locationPermissionBar.setFragmentManager(childFragmentManager)
        }
    }


    override fun onDemoItemClicked(demoItem: DemoMenuItem) {
        // Only handle Health Thermometer demo
        BLEUtils.GATT_DEVICE_SELECTED = demoItem.connectType
        // Prevent multiple dialogs
        val existingDialog = childFragmentManager.findFragmentByTag("select_device_dialog")
        if (existingDialog == null || !existingDialog.isVisible) {
            selectDeviceDialog = SelectDeviceDialog.newDialog(demoItem.connectType)
            selectDeviceDialog?.show(childFragmentManager, "select_device_dialog")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        selectDeviceDialog = null
    }

}
