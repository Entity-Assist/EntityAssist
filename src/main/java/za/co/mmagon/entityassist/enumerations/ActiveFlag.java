package za.co.mmagon.entityassist.enumerations;

/**
 * Active Range ordered from Unknown -> Removed -> Archived/Visible -> Active -> Highlighted -> Permanent
 * <p>
 * Meant as text with indexer for support on Gen 3 and Gen 4 databases
 */
public enum ActiveFlag
{
	/**
	 * UnknownRange
	 */
	Unknown,
	Unspecified,
	/**
	 * RemovedRange
	 **/
	Deleted,
	Hidden,
	Invisible,
	/**
	 * VisibleRange
	 */
	Archived,
	LongTermStorage,
	MidTermStorage,
	ShortTermStorage,
	/**
	 * ActiveRange
	 */
	Active,
	Current,
	/**
	 * HighlightedRange
	 **/
	Important,
	Highlighted,
	/**
	 * PermanentRange
	 */
	Always,
	Permanent


}
