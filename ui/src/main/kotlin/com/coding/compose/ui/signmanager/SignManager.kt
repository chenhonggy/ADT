package com.coding.compose.ui.signmanager

import RadioGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coding.compose.base.Button
import com.coding.compose.base.Dialog
import com.coding.compose.base.Toast
import com.coding.dec.utils.SignUtils


@Composable
fun SignManagerDialog(show: MutableState<Boolean>) {
    Dialog(title = "sign manager", state = show) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (show.value) {
                val list = remember { mutableStateListOf<String>() }
                list.clear()
                for (item in SignUtils.getSignList()) {
                    list.add(item.name)
                }
                val select = remember { mutableStateOf("choose sign") }
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.height(190.dp).verticalScroll(scrollState)) {
                    RadioGroup(select, list)
                }

                val addSign_show = remember { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button("delete sign") {
                        val signBean = SignUtils.getSign(select.value) ?: return@Button
                        if (signBean.name == "adt.jks") {
                            Toast.showMsg(window, "the default signature can't be deleted!")
                            return@Button
                        }
                        if (signBean.name == "") {
                            return@Button
                        }
                        SignUtils.deleteSign(signBean)
                        list.remove(select.value)
                    }

                    Button("add sign") {
                        addSign_show.value = true
                    }
                }
                AddSignDialog(addSign_show)
                //界面关闭后，刷新list
                if (!addSign_show.value) {
                    list.clear()
                    for (item in SignUtils.getSignList()) {
                        list.add(item.name)
                    }
                }
            }
        }
    }
}


