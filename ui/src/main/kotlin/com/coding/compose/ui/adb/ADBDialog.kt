package com.coding.compose.ui.adb

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.coding.compose.base.*
import com.coding.compose.listener.OnCheckListener
import com.coding.compose.listener.OnSelectListener
import com.coding.dec.AdbTool
import com.coding.dec.utils.Suffix
import javax.swing.JFileChooser

@OptIn(ExperimentalUnitApi::class)
@Composable
fun ADBDialog(show: MutableState<Boolean>) {
    Dialog(title = "adb", state = show) {
        Row {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val btnLabel = remember { mutableStateOf("choose device") }
                val selectedDevice = remember { mutableStateOf("") }
                val showDevicesList = remember { mutableStateOf(false) }
                DevicesListDialog(showDevicesList, object : OnSelectListener {
                    override fun onSelected(name: String) {
                        selectedDevice.value = name
                        btnLabel.value = name
                        showDevicesList.value = false
                    }
                })
                ClickableText(text = AnnotatedString(btnLabel.value), style = TextStyle(
                    color = Color.Blue, fontSize = TextUnit(16.0f, TextUnitType.Sp)
                ), onClick = {
                    showDevicesList.value = true
                })
                val debug = remember { mutableStateOf(false) }
                CheckBox("debug", debug)
                Button("install apk") {
                    if (selectedDevice.value.isEmpty()) {
                        Toast.showMsg(window, "Please select the device first.")
                        return@Button
                    }
                    FileChooser.newInstance(window,
                        JFileChooser.FILES_ONLY,
                        "choose apk",
                        Suffix.APK,
                        object : FileChooser.OnFileSelectListener {
                            override fun onSelected(path: String) {
                                println("install apk:" + AdbTool.installApk(selectedDevice.value, debug.value, path))
                            }
                        })
                }


                val pkgNameLabel = remember { mutableStateOf("choose package name") }
                val selectedPkg = remember { mutableStateOf("") }
                val showPkgNamesList = remember { mutableStateOf(false) }
                val mode = remember { mutableStateOf("") }
                AppPkgListDialog(selectedDevice, mode, showPkgNamesList, object : OnSelectListener {
                    override fun onSelected(name: String) {
                        selectedPkg.value = name
                        pkgNameLabel.value = name
                        showPkgNamesList.value = false
                    }
                })

                val only_system = remember { mutableStateOf(false) }
                val only_third = remember { mutableStateOf(false) }
                CheckBox("only system app", only_system, listener = object : OnCheckListener {
                    override fun onCheckedChange(checked: Boolean) {
                        if (checked) {
                            only_third.value = false
                            mode.value = AdbTool.ONLY_SYSTEM_APP
                            return
                        }
                        mode.value = AdbTool.ALL_APP
                    }
                })

                CheckBox("only third app", only_third, listener = object : OnCheckListener {
                    override fun onCheckedChange(checked: Boolean) {
                        if (checked) {
                            only_system.value = false
                            mode.value = AdbTool.ONLY_THIRD_APP
                            return
                        }
                        mode.value = AdbTool.ALL_APP
                    }
                })

                ClickableText(text = AnnotatedString(pkgNameLabel.value), style = TextStyle(
                    color = Color.Blue, fontSize = TextUnit(16.0f, TextUnitType.Sp)
                ), onClick = {
                    showPkgNamesList.value = true
                })
                Row(horizontalArrangement = Arrangement.spacedBy(25.dp)) {
                    Button("extract apk") {
                        if (selectedDevice.value.isEmpty() || selectedPkg.value.isEmpty()) {
                            Toast.showMsg(window, "Please select a device and package name.")
                            return@Button
                        }
                        FileChooser.newInstance(window,
                            JFileChooser.DIRECTORIES_ONLY,
                            "choose dir",
                            object : FileChooser.OnFileSelectListener {
                                override fun onSelected(path: String) {
                                    AdbTool.extractApk(selectedDevice.value, selectedPkg.value, path)
                                }
                            })
                    }
                }
            }
        }
    }
}