import "./FilterMenu.css"

export type FilterOption = { value: string; label: string }

function FilterMenu({
  label,
  value,
  options,
  tone,
  open,
  onToggle,
  onChange,
}: {
  label: string
  value: string
  options: FilterOption[]
  tone: "dark" | "warm"
  open: boolean
  onToggle: () => void
  onChange: (value: string) => void
}) {
  const selected = options.find((option) => option.value === value)
  const active = selected != null && selected.value !== ""
  const caption = active ? selected.label : label

  return (
    <div className={`filter filter--${tone}${active ? " filter--active" : ""}`}>
      <button type="button" aria-expanded={open} onClick={onToggle}>
        {caption}
        <span aria-hidden="true">{open ? "▴" : "▾"}</span>
      </button>
      {open && (
        <ul>
          {options.map((option) => (
            <li key={option.label}>
              <button
                type="button"
                className={option.value === value ? "is-selected" : undefined}
                onClick={() => onChange(option.value)}
              >
                {option.label}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default FilterMenu
