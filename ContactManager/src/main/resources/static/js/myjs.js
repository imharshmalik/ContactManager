const toggleSidebar=() =>
{
	if($('.sidebar').is(":visible"))
	{
		$('.sidebar').css("display", "none");
		$('.content').css("margin-left", "0%");
		$('.fas').css("display", "block");
	}
	else
	{
		$('.sidebar').css("display", "block");
		$('.content').css("margin-left", "20%");
		$('.fas').css("display", "none");
	}
}

function deleteContact(contactId)
{
	swal
	({
		title: "Do you want to delete this contact?",
		icon: "warning",
		buttons: true,
		dangerMode: true,
	})
	.then((willDelete) =>
	{
		if(willDelete)
		{
			window.location = "/user/delete-contact/" + contactId;
		}	
	})
}

const searchContact=() =>
{
	let searchKeyword = $("#search-input").val();
	
	if(searchKeyword == "")
	{
		$(".search_result").hide();
	}
	else
	{
		let url = `http://localhost:8181/search/${searchKeyword}`;
		
		fetch(url)
			.then((response) => 
				{
					return response.json();
				})
			.then((searchResult) =>
				{
					console.log(searchResult);
					
					let text = `<div class="list-group">`;					
					searchResult.forEach((contact) =>
					{
						text += `<a href='/user/contact/${contact.id}' class='list-group-item list-group-item-action'>${contact.name}</a>`
					});					
					text += `</div>`;
									
					$(".search_result").html(text);
					$(".search_result").show();
				});		
	}
}

function goBack()
{
	 window.history.back();
}