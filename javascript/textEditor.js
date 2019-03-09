var currentPath = window.location.hash.substring(1);
console.log("Current Path: "+currentPath);
var filename = window.location.search;
console.log("Filename: "+filename);

function saveFile(){
  //Do post here
  var textarea = document.getElementById("textEditor");
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
    }
  };
  xhttp.open("POST", "/saveFile?filename="+filename, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.send("body="+textEditor);
}

try{
  document.getElementById("saveFile").addEventListener("click", saveFile);
}
catch{
  //
}

function goBack(){
  window.location.replace("/home.html#"+currentPath);
}
document.getElementById("Backbtn").addEventListener("click", goBack);
