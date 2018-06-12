
function showStarsWithName(rating, name) {
	for (var i = 0; i < 5; i++) {
		var starRate = rating - i * 2;
		if (starRate <= 0) {
			$('#' + name + i).css('background-image', 'url(\'resources/img/no_star.png\')');
		} else if (starRate == 1) {
			$('#' + name + i).css('background-image', 'url(\'resources/img/half_star.png\')');
		} else {
			$('#' + name + i).css('background-image', 'url(\'resources/img/full_star.png\')');
		}
	}
}

function showStars(rating) {
	showStarsWithName(rating, 'rating_star_');
}

function setRating(rating) {
	$('#rating_rating').val(rating);
}

function leaveStar() {
	showStars($('#rating_rating').val());
}