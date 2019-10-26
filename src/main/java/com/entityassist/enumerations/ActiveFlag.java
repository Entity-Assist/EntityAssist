package com.entityassist.enumerations;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Active Range ordered from Unknown to Removed to Archived/Visible to Active to Highlighted to Permanent
 * <p>
 * Meant as text with indexer for support on Gen 3 and Gen 4 databases
 */
public enum ActiveFlag
{
	/**
	 * UnknownRange
	 */
	Unknown("The Active Flag Status is Unknown"),
	/**
	 * If the item is unspecified
	 */
	Unspecified("The Active Flag Status is not specified"),
	/**
	 * RemovedRange
	 **/
	Deleted("The given record has been deleted"),
	/**
	 * If the item is simply hidden
	 */
	Hidden("The record has been hidden from queries"),
	/**
	 * If the item should be invisible
	 */
	Invisible("This record has been deemed invisible"),
	/**
	 * If the active status is errored
	 */
	Errored("The record is marked as an Error"),
	/**
	 * VisibleRange
	 */
	Archived("This record is enabled, and archived out of current queries"),
	/**
	 * If the item is saved for long term storage
	 */
	LongTermStorage("This record is marked active for archiving after 90 days"),
	/**
	 * If the item is saved for mid term storage
	 */
	MidTermStorage("This record is marked active for archiving after 60 days"),
	/**
	 * If the item is saved for short term storage
	 */
	ShortTermStorage("This record is marked active for archiving after 30 days"),
	/**
	 * If the item is resolved
	 */
	Resolved("This record marks the resolution of a previously Errored record"),
	/**
	 * If the item is completed
	 */
	Completed("This record is marked as complete"),
	/**
	 * ActiveRange
	 */
	Pending("This record is currently pending for some other activity"),
	/**
	 * ActiveRange
	 */
	Active("This record is currently active for querying"),
	/**
	 * If the item is current
	 */
	Current("This record is marked as the currently active record"),
	/**
	 * HighlightedRange
	 **/
	Important("This record is deemed as important "),
	/**
	 * If the item is highlighted
	 */
	Highlighted("This record should be highlighted"),
	/**
	 * PermanentRange
	 */
	Always("This record should always be shown but is modifiable"),
	/**
	 * If the item is permanent
	 */
	Permanent("This record is permanent");

	private static final Set<ActiveFlag> PermanentRange = EnumSet.of(Always, Permanent);
	private static final Set<ActiveFlag> HighlightedRangeAndUp = EnumSet.of(Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> HighlightedRange = EnumSet.of(Important, Highlighted);
	private static final Set<ActiveFlag> ArchivedRange = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage);
	private static final Set<ActiveFlag> ActiveRangeAndUp = EnumSet.of(Active, Current, Important, Highlighted, Pending, Always, Permanent);
	private static final Set<ActiveFlag> ActiveRange = EnumSet.of(Pending, Active, Current);
	private static final Set<ActiveFlag> VisibleRangeAndUp = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved, Completed, Active, Current,
	                                                                    Important,
	                                                                    Highlighted, Pending, Always, Permanent);
	private static final Set<ActiveFlag> VisibleRange = EnumSet.of(Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved, Completed);
	private static final Set<ActiveFlag> RemovedRangeAndUp = EnumSet.of(Deleted, Hidden, Invisible, Archived, LongTermStorage, MidTermStorage, ShortTermStorage, Resolved,
	                                                                    Completed, Errored, Pending,
	                                                                    Active, Current, Important, Highlighted, Always, Permanent);
	private static final Set<ActiveFlag> RemovedRange = EnumSet.of(Deleted, Hidden, Invisible, Errored);


	private String description;


	ActiveFlag(String description)
	{
		this.description = description;
	}

	/**
	 * Returns the permanent range of values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getPermanentRange()
	{
		return PermanentRange;
	}

	/**
	 * Returns the highlighted and up range of values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRangeAndUp()
	{
		return HighlightedRangeAndUp;
	}

	/**
	 * Returns the highlighted range of visible values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getHighlightedRange()
	{
		return HighlightedRange;
	}

	/**
	 * Returns the active range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRangeAndUp()
	{
		return ActiveRangeAndUp;
	}

	/**
	 * Returns the active range only
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getActiveRange()
	{
		return ActiveRange;
	}

	/**
	 * Returns the visible range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRangeAndUp()
	{
		return VisibleRangeAndUp;
	}

	/**
	 * Returns the visible range
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getVisibleRange()
	{
		return VisibleRange;
	}

	/**
	 * Returns the removed range and up values
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRangeAndUp()
	{
		return RemovedRangeAndUp;
	}

	/**
	 * Returns the removed range
	 *
	 * @return ActiveFlags
	 */
	@NotNull
	public static Set<ActiveFlag> getRemovedRange()
	{
		return RemovedRange;
	}

	/**
	 * Getter for property 'archivedRange'.
	 *
	 * @return Value for property 'archivedRange'.
	 */
	public static Set<ActiveFlag> getArchivedRange()
	{
		return ArchivedRange;
	}

	/**
	 * Returns the set as a list of strings
	 *
	 * @param flags
	 *
	 * @return
	 */
	public static Set<String> activeFlagToStrings(Set<ActiveFlag> flags)
	{
		Set<String> output = new LinkedHashSet<>();
		for (ActiveFlag flag : flags)
		{
			output.add(flag.toString());
		}
		return output;
	}

	@Override
	public String toString()
	{
		return name();
	}

	/**
	 * Getter for property 'description'.
	 *
	 * @return Value for property 'description'.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Setter for property 'description'.
	 *
	 * @param description
	 * 		Value to set for property 'description'.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
