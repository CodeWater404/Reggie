/* 自定义trim */
function trim (str) {  //删除左右两端的空格,自定义的trim()方法
  return str == undefined ? "" : str.replace(/(^\s*)|(\s*$)/g, "")
}

//获取url地址上面的参数；比如http://localhost:8080/backend/page/member/add.html?id=1523323100833804289
function requestUrlParam(argname){
  var url = location.href
  //  从？后面开始以&开始分隔  比如id=1&name=2&sex=1
  var arrStr = url.substring(url.indexOf("?")+1).split("&")
  for(var i =0;i<arrStr.length;i++)
  {
      //找到需要的参数，传过来的argname
      var loc = arrStr[i].indexOf(argname+"=")
      if(loc!=-1){
          return arrStr[i].replace(argname+"=","").replace("?","")
      }
  }
  return ""
}
