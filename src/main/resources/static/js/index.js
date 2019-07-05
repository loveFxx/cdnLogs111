function getTitleExpandFF(value){
	var tempTitleExpandFF = value+"";
	var useragent = navigator.userAgent;
	if(useragent.indexOf("Firefox") > -1){
		if(value.length>60){
			var titles = [];
			for (var i = 0; i < value.length; i = i + 60) {
				if (i + 60 > value.length) {
					titles.push(value.substring(i,value.length))
				} else {
					titles.push(value.substring(i,i + 60))
				}
			}
			tempTitleExpandFF = "";
			for(var titlesIndex in titles){
				tempTitleExpandFF += titles[titlesIndex] + "&#13;"
			}
		}
	}
	return tempTitleExpandFF.replace(/ /g,'&#32;');
}

function getTitleExpandFFWhenCdn(row,value){
	var tempTitle = getTitleExpandFF(value);
	var str = row.note;
	str = str.replace(/ /g,'&#32;').split('&#32;')[0];
	if(str!="200"&&str!="706"){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}else{
		return '<span title='+tempTitle+'>' + value + '</span>';
	}
}

function getTitleExpandFFWhenRating(row,value){
	var tempTitle = getTitleExpandFF(value);

	return  tempTitle ;

}

function getTitleExpandFFWhenClient(row,value){
	var tempTitle = getTitleExpandFF(value);
	var ftvCpu = row.ftvCpu.split('%')[0];
	var ftvMemory = row.ftvMemory.split('M')[0];
	if(ftvCpu>10){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}
	if(ftvMemory>150){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}
	return '<span title='+tempTitle+'>' + value + '</span>';
}

function getTitleExpandFFWhenClientLogs(isException,value){
	var tempTitle = getTitleExpandFF(value);
	if(isException==-1){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}
	return '<span title='+tempTitle+'>' + value + '</span>';
}

function getTitleExpandFFWhenLogin(row,value){
	var tempTitle = getTitleExpandFF(value);
	if(row.url!=''&&row.startTime!=''&&row.endTime!=''&&row.returnCode!=0){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}
	return '<span title='+tempTitle+'>' + value + '</span>';
}

function getTitleExpandFFWhenVideoInfo(row,value){
	var tempTitle = getTitleExpandFF(value);
	if(row.playErrorCode.split(':').length>1){
		return '<span style="color:red" title='+tempTitle+'>' + value + '</span>';
	}
	return '<span title='+tempTitle+'>' + value + '</span>';
}

function getTitleExpandFFWhenCMS(value){
	var tempTitle = getTitleExpandFF(value);
	return '<span title='+tempTitle+'>' + value + '</span>';
}

function dataCopy(row){
	var selection = window.getSelection();
	var range = document.createRange();
	range.selectNode(row[0]);
	selection.removeAllRanges();
	selection.addRange(range);
	document.execCommand("Copy");
	alert("复制成功！！！")
}

function getStrByDate(toStr){
	var fromInputTimeStr;
	var yy = toStr.getFullYear();      //年
	var mm = toStr.getMonth() + 1;     //月
	var dd = toStr.getDate();          //日
	var hh = toStr.getHours();         //时
	var ii = toStr.getMinutes();       //分
	var ss = toStr.getSeconds();       //秒
	var fromInputTimeStr = yy + "-";
	if(mm < 10) fromInputTimeStr += "0";
	fromInputTimeStr += mm + "-";
	if(dd < 10) fromInputTimeStr += "0";
	fromInputTimeStr += dd + " ";
	if(hh < 10) fromInputTimeStr += "0";
	fromInputTimeStr += hh + ":";
	if (ii < 10) fromInputTimeStr += '0'; 
	fromInputTimeStr += ii + ":";
	if (ss < 10) fromInputTimeStr += '0'; 
	fromInputTimeStr += ss;
	return fromInputTimeStr;
}

function getDayStrByDate(toStr){
	var fromInputTimeStr;
	var yy = toStr.getFullYear();      //年
	var mm = toStr.getMonth() + 1;     //月
	var dd = toStr.getDate();          //日
	var fromInputTimeStr = yy + "-";
	if(mm < 10) fromInputTimeStr += "0";
	fromInputTimeStr += mm + "-";
	if(dd < 10) fromInputTimeStr += "0";
	fromInputTimeStr += dd;
	return fromInputTimeStr;
}

function getDateByStr(toDateTime){
	var temptoDateTime = toDateTime;
	if(temptoDateTime!=0){
		temptoDateTime = temptoDateTime.substring(0,19);
		temptoDateTime = temptoDateTime.replace(/-/g,'/');
	}
	return temptoDateTime;
}

function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}
