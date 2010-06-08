var selected = exchanger.getSelection();
if(selected != null) {
	exchanger.replaceSelection(selected.toLowerCase());
}
else {
	exchanger.showAlert("Please select some text to convert to lower case");
}

