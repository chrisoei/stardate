' You cannot name the macro the same as the filename (StarDate), lest OpenOffice confuse them.
' Note that this is an approximation of the definitive Java version, and has not been extensively tested.
FUNCTION getStarDateFromUTC(d)
	y0 = YEAR(d)
	t0 = DateValue(y0 & "-01-01 00:00:00")
	t1 = DateValue((y0 + 1) & "-01-01 00:00:00")
	getStarDateFromUTC = y0 + (d - t0)/(t1 - t0)
End FUNCTION
