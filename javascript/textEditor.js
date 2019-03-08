var currentPath = window.location.hash.substring(1)

function saveFile(){
  //Do post here
}

document.getElementById("saveFile").addEventListener("click", saveFile);

function goBack(){
  window.location.replace("/home.html#"+currentPath);
}
document.getElementById("Backbtn").addEventListener("click", goBack);
