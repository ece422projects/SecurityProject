// Get the modal
var modal;
var closeList = document.getElementsByClassName("close");

// Get the button that opens the modal
var newGrp = document.getElementById("newGrp");

// Get the <span> element that closes the modal
var groupLinks = document.getElementsByClassName("groupLink");

var i;
for(i=0; i<groupLinks.length; i++){
  groupLinks[i].addEventListener("click",showGrpModal(groupLinks[i]));
}

for(i=0; i<closeList.length; i++){
  closeList[i].addEventListener("click",closeModal);
}
// When the user clicks on <span> (x), close the modal
function closeModal() {
  modal.style.display = "none";
}
// When the user clicks on the button, open the modal
newGrp.onclick = function() {
  modal =  document.getElementById('newGrpModal');
  modal.style.display = "block";
}

function showGrpModal(element){
  return function() {
    var groupName = element.innerHTML;
    console.log(groupName);
    document.getElementById('groupName').innerHTML = groupName;
    modal = document.getElementById('grpModal');
    modal.style.display = "block";
  }
}

var newFileBtn = document.getElementById('newFile');
newFileBtn.addEventListener("click", function(){
  modal = document.getElementById('fileModal');
  modal.style.display = "block";
});

var newFolderBtn = document.getElementById('newFolder');
newFolderBtn.addEventListener("click", function() {
  modal = document.getElementById('folderModal');
  modal.style.display = "block";
});



// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
  if (!event.target.matches('#fileDropdown')) {
    console.log("Closing the File Dropdown");
    var dropdowns = document.getElementById("fileDropdowns");
    dropdowns.style.display = "none";
  }
  if (!event.target.matches('#group')) {
    var dropdowns = document.getElementById("groups");
    dropdowns.style.display = "none";
  }
  if (event.target == modal) {
    modal.style.display = "none";
  }
}
